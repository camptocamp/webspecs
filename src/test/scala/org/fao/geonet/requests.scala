package org.fao.geonet

import org.apache.http.client.methods.{HttpRequestBase, HttpGet}
import org.apache.http.impl.client.DefaultHttpClient

trait Apply[Out] {
  self : Request[Any,Out] =>
  def apply():Response[Out] = apply(None)
}
trait Request[-In, +Out] {
  def then [A,B] (next: Request[Out,A]) : Request[In, A] = ChainedRequest(this,next)
  def compose [A,B] (before: Request[A,In]) : Request[A, Out] = ChainedRequest(before,this)
  def apply (in: In) : Response[Out]
}

object ChainedRequest {
  def apply[A,B,C](first:Request[A,B],second:Request[B,C]) = {
    new ChainedRequest(first,second)
  }
}

class ChainedRequest[-A,B,+C](first:Request[A,B],second:Request[B,C]) extends Request[A,C] {
  def apply(in: A) = {
    val result1 = first.apply(in)
    second.apply(result1.value)
  }
}
abstract class AbstractRequest[-In, +Out]
    (request:HttpRequestBase, valueFactory:ValueFactory[In,Out]) extends Request[In,Out] {

  def apply(in: In) = {
    val client = new DefaultHttpClient()
    try {
      Log.apply(Log.Connection,"Executing "+request.getMethod+": "+request.getURI)
      Log.apply(Log.Headers,"Headers:"+request.getAllHeaders.map{h => "("+h.getName+","+h.getValue+")"}.mkString("; "))
      val httpResponse = client.execute(request);
      val basicValue = BasicHttpValue(httpResponse)
      val value = valueFactory(this,in,basicValue)
      new BasicHttpResponse(basicValue,value)
    } finally {
      client.getConnectionManager().shutdown()
    }
  }
}

abstract class AbstractGetRequest[-In, +Out](uri:String,valueFactory:ValueFactory[In,Out]) extends AbstractRequest[In,Out](new HttpGet(uri),valueFactory) {

}

case class Get(uri:String) extends AbstractGetRequest[Any,XmlValue](uri,XmlValueFactory) with Apply[XmlValue]