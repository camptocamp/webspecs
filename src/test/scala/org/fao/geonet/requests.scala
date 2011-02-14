package org.fao.geonet

import java.io.IOException
import org.apache.http.client.methods.{HttpRequestBase, HttpGet}
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.HttpClient
import collection.JavaConverters._

trait ExecutionContext {
  def httpClient:HttpClient
  var modifications:List[RequestModification] = Nil
  def execute(request:HttpRequestBase) = {
    modifications foreach {_(request)}

    Log.apply(Log.Connection,"Executing "+request.getMethod+": "+request.getURI)
    Log.apply(Log.Headers,"Headers:"+request.getAllHeaders.map{h => "("+h.getName+","+h.getValue+")"}.mkString("; "))
    httpClient match {
      case client:DefaultHttpClient =>
        val cookies = client.getCookieStore.getCookies.asScala mkString ","
        Log.apply(Log.Headers,"Cookies: "+cookies)
      case client =>
        Log.apply(Log.Headers,"Cookies: Not a DefaultHttpClient - "+client.getClass.getName)
    }


    httpClient.execute(request);
  }
}

case class DefaultExecutionContext(val httpClient:HttpClient = new DefaultHttpClient()) extends ExecutionContext

trait Apply[Out] {
  self : Request[Any,Out] =>
  def apply()(implicit context:ExecutionContext):Response[Out] = apply(None)
}
trait Request[-In, +Out] {
  def then [A,B] (next: Request[Out,A]) : Request[In, A] = ChainedRequest(this,next)
  def then [A,B] (next: Response[Out] => Request[Out,A]) : Request[In, A] = ChainedRequest(this,next)
  def compose [A,B] (before: Request[A,In]) : Request[A, Out] = ChainedRequest(before,this)
  def apply (in: In)(implicit context:ExecutionContext) : Response[Out]
}

object ChainedRequest {
  class ConstantRequestFunction[A,B](request:Request[A,B]) extends Function[Response[A],Request[A,B]] {
    override def apply(v1: Response[A]) = request
    override def toString() = request.toString
  }
  def apply[A,B,C](first:Request[A,B],second:Request[B,C]) = {
    new ChainedRequest(first,new ConstantRequestFunction(second))
  }
  def apply[A,B,C](first:Request[A,B],second:Response[B] => Request[B,C]) = {
    new ChainedRequest(first,second)
  }
}
class ChainedRequest[-A,B,+C] private(first:Request[A,B],second:Function[Response[B], Request[B,C]]) extends Request[A,C] {
  def apply (in: A)(implicit context:ExecutionContext) = {
    first.apply(in) match {
      case response if response.basicValue.responseCode > 399 =>
        val basicValue = response.basicValue
        throw new IOException("Executing "+first+" failed with a "+basicValue.responseCode+" responseCode, message = "+basicValue.responseMessage)//+"\ntext:\n"+response.text)
      case response =>
        second(response).apply(response.value)
    }
  }
}

abstract class AbstractRequest[-In, +Out]
    (request:HttpRequestBase, valueFactory:ValueFactory[In,Out]) extends Request[In,Out] {

  final def apply (in: In)(implicit context:ExecutionContext) = {
    val httpResponse = context.execute(request)
    val basicValue = BasicHttpValue(httpResponse)
    val value = valueFactory(this,in,basicValue)
    val response = new BasicHttpResponse(basicValue,value)
    response
  }
}

abstract class AbstractGetRequest[-In, +Out](uri:String,valueFactory:ValueFactory[In,Out]) extends AbstractRequest[In,Out](new HttpGet(uri),valueFactory) {

}

case class Get(uri:String) extends AbstractGetRequest[Any,XmlValue](uri,XmlValueFactory) with Apply[XmlValue]