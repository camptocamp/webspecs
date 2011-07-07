package c2c.webspecs

import accumulating.AccumulatingRequest1
import java.io.IOException
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.HttpClient
import collection.JavaConverters._
import org.apache.http.entity.StringEntity
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.message.BasicNameValuePair
import org.apache.http.conn.ClientConnectionManager
import org.apache.http.protocol.{BasicHttpContext, HttpContext}
import org.apache.http.HttpResponse
import org.apache.http.client.methods.{HttpUriRequest, HttpPost, HttpRequestBase, HttpGet}
import java.net.URI
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager

object ExecutionContext {
  def apply[R](context:ExecutionContext)(f : ExecutionContext => R):R = {
    try {f(context)}
    finally {
      context.httpClient.getConnectionManager.shutdown
    }
  }
  def withDefault[R] (f : ExecutionContext => R):R =
    apply(DefaultExecutionContext())(f)
  case class Response(httpResponse:HttpResponse, finalHost:String, finalURL:Option[URI])
}

trait ExecutionContext {

  var currentUser:Option[(String,ExecutionContext.Response)] = None

  def httpClient:HttpClient
  def createHttpContext:() => HttpContext
  val conn: ClientConnectionManager = httpClient.getConnectionManager
  var modifications:List[RequestModification] = Nil
  def close() = httpClient.getConnectionManager.shutdown
  def execute(request:HttpRequestBase) = request match {
    case r:LoginRequest if currentUser.exists {_._1 == r.user} =>
      currentUser.get._2
    case _ =>
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

      val localContext = createHttpContext()
      val response = httpClient.execute(request,localContext);
      val finalHost = localContext.getAttribute(org.apache.http.protocol.ExecutionContext.HTTP_TARGET_HOST).toString
      val finalURI = localContext.getAttribute(org.apache.http.protocol.ExecutionContext.HTTP_REQUEST) match {
        case req:HttpUriRequest => Some(req.getURI)
        case _ => None
      }

      Log.apply(Log.Headers,"URI after request: "+request.getURI)
      Log.apply(Log.Headers,"Response Headers: "+response.getAllHeaders.map{h => "("+h.getName+","+h.getValue+")"}.mkString("; "))

      httpClient match {
        case client:DefaultHttpClient =>
          val cookies = client.getCookieStore.getCookies.asScala mkString ","
          Log.apply(Log.Headers,"Cookies after request: "+cookies)
        case client =>
          Log.apply(Log.Headers,"Cookies: Not a DefaultHttpClient - "+client.getClass.getName)
      }

      val finalResponse = ExecutionContext.Response(response,finalHost,finalURI)

      request match {
        case r:LoginRequest =>
          currentUser = Some((r.user, finalResponse))
        case _ =>
          ()
      }

      finalResponse
  }
}

case class DefaultExecutionContext(val httpClient:HttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager()),
                                   val createHttpContext: () => HttpContext = () => new BasicHttpContext) extends ExecutionContext {
  httpClient.getConnectionManager().getSchemeRegistry().register(SSLUtilities.fakeSSLScheme(443))
  httpClient.getConnectionManager().getSchemeRegistry().register(SSLUtilities.fakeSSLScheme(8443))
  httpClient.getConnectionManager().asInstanceOf[ThreadSafeClientConnManager].setDefaultMaxPerRoute(10)
  httpClient.getConnectionManager().asInstanceOf[ThreadSafeClientConnManager].setMaxTotal(50)
}

object Request {
  def const[A](in:A) = new Request[Any,A] {
    def apply(empty: Any)(implicit context: ExecutionContext) = Response(in)
  }
}
trait Request[-In, +Out] {
  def then [A,B] (next: Request[Out,A]) : Request[In, A] =
    ChainedRequest(this,next)
  def then [A,B] (next: Response[Out] => Request[Out,A]) : Request[In, A] =
    ChainedRequest(this,next)
  def vThen [A,B] (next: Out => Request[Out,A]) : Request[In, A] =
    this then (r => next(r.value))
  def startTrackingThen [A,B] (next: Request[Out,A]) : AccumulatingRequest1[In, Out, A] =
    startTrackingThen(new ChainedRequest.ConstantRequestFunction(next))
  def startTrackingThen [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest1[In, Out, A] =
    new AccumulatingRequest1(next, AccumulatingRequest.Elem(this,true))
  def setIn[A <: In](in:A):Request[Any,Out] = Request.const(in) then this
  def map[A] (mapping: Out => A):Request[In,A] = {
    val outer = this;
    new Request[In,A] {
      def apply(in: In)(implicit context: ExecutionContext) = outer(in).map(mapping)
    }
  }
  def apply (in: In)(implicit context:ExecutionContext) : Response[Out]
  def assertPassed(in:In)(implicit context:ExecutionContext) = apply(in) match {
    case response if response.basicValue.responseCode > 399 =>
      throw new AssertionError(toString+" did not complete correctly, reponseCode="+
        response.basicValue.responseCode+" message: "+
        response.basicValue.responseMessage)
    case response => response
  }
}
object NoRequest extends Request[Any,Null] {
  def apply(in: Any)(implicit context: ExecutionContext) = EmptyResponse
  override def toString() = "NoRequest"
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
    (valueFactory:ValueFactory[In,Out]) extends Request[In,Out] {
  def request(in:In):HttpRequestBase
  final def apply (in: In)(implicit context:ExecutionContext) = {
    val httpResponse = context.execute(request(in))
    val basicValue = BasicHttpValue(httpResponse)
    val value = valueFactory.createValue(this,in,basicValue,context)
    val response = new BasicHttpResponse(basicValue,value)
    response
  }
}

abstract class Param[-In,+Out](val name:String,val value:In => Out)
object Param {
  def stringMapping[B] = (p:(String,B)) => P(p._1,p._2.toString)
}
object P {
  def apply[Out](pair:(String,Out)):P[Out] = P(pair._1,pair._2)
}
case class P[+Out](n:String, v:Out) extends Param[Any,Out](n, _ => v)

object SP {
  def apply(pair:(String,Any)):SP = SP(pair._1,pair._2)
}
case class SP(n:String, v:Any) extends Param[Any,String](n, _ => v.toString)

case class InP[-In,+Out](n:String,f:In => Out) extends Param[In,Out](n,f)
case class IdP[In](n:String) extends Param[In,String](n,in => in.toString)

abstract class AbstractGetRequest[-In, +Out](uri:String,valueFactory:ValueFactory[In,Out],params:Param[In,String]*)
  extends AbstractRequest[In,Out](valueFactory) {
  def request(in:In) = {
    val stringParams = params.map(p => p.name -> p.value(in))
    new HttpGet(Config.resolveURI(uri,stringParams:_*))
  }
}



abstract class AbstractXmlPostRequest[-In, +Out](uri:String, valueFactory:ValueFactory[In,Out])
  extends AbstractRequest(valueFactory) {
  override def request(in:In) = {
    Log.apply(Log.RequestXml, xmlData.toString)
    val post = new HttpPost(Config.resolveURI(uri))
    post.setEntity(new StringEntity(xmlData.toString));
    post.setHeader("Content-type", "text/xml");
    post
  }

  def xmlData:xml.NodeSeq

  override def toString() = "XmlRequest("+uri+")"
}

abstract class AbstractFormPostRequest[-In, +Out](val uri:String,valueFactory:ValueFactory[In,Out],params:Param[In,String]*)
  extends AbstractRequest(valueFactory) {
  def request(in:In) = {
    val post = new HttpPost(Config.resolveURI(uri))
    Log.apply(Log.RequestForm, params mkString ("\t","\n\t",""))
    val formParams = params.map{p => new BasicNameValuePair(p.name,p.value(in))}.toList
    post.setEntity(new UrlEncodedFormEntity(formParams.asJava,"UTF-8"))
    post
  }
}

case class GetRequest(uri:String, params:(String,Any)*) extends AbstractGetRequest[Any,XmlValue](uri,XmlValueFactory,params.map(Param.stringMapping):_*)
case class FormPostRequest(override val uri:String, form:(String,Any)*) extends AbstractFormPostRequest[Any,XmlValue](uri,XmlValueFactory,form.map(Param.stringMapping):_*)
case class XmlPostRequest(uri:String, xmlData:xml.NodeSeq) extends AbstractXmlPostRequest[Any,XmlValue](uri,XmlValueFactory)
