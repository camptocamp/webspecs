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

object Request {
  def const[A](in:A) = new Request[Any,A] with AnyRequest[A] {
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
