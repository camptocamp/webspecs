package c2c.webspecs

import collection.JavaConverters._
import org.apache.http.HttpResponse
import java.net.URI
import org.apache.http.client.HttpClient
import org.apache.http.protocol.HttpContext
import org.apache.http.conn.ClientConnectionManager
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpUriRequest
import c2c.webspecs.login.LoginRequest
import org.apache.http.impl.client.BasicAuthCache
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.client.protocol.ClientContext
import org.apache.http.HttpHost

object ExecutionContext {
  def apply[R](context:ExecutionContext)(f : ExecutionContext => R):R = {
    try {f(context)}
    finally {
      context.close
    }
  }
  def withDefault[R] (f : ExecutionContext => R):R =
    apply(new DefaultExecutionContext())(f)
  case class Response(httpResponse:HttpResponse, finalHost:String, finalURL:Option[URI])
}

trait ExecutionContext {
  def createNew:ExecutionContext

  var currentUser:Option[(LoginRequest,ExecutionContext.Response)] = None

  def httpClient:HttpClient
  def createHttpContext:() => HttpContext
  val conn: ClientConnectionManager = httpClient.getConnectionManager
  var modifications:List[RequestModification] = Nil
  def close() = 
    httpClient.getConnectionManager.shutdown
  def execute(request:HttpRequestBase) = request match {
    case r:LoginRequest if currentUser.exists {_._1.user == r.user} =>
      Log.apply(Log.Connection, "Skipping login request since user is already logged in")
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
      // Create AuthCache instance
      val authCache = new BasicAuthCache();
      // Generate BASIC scheme object and add it to the local auth cache
      val basicAuth = new BasicScheme();
      authCache.put(new HttpHost(request.getURI().getHost()), basicAuth);
      // Add AuthCache to the execution context
      localContext.setAttribute(ClientContext.AUTH_CACHE, authCache);        

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
          currentUser = Some((r, finalResponse))
        case _ =>
          ()
      }

      finalResponse
  }
}