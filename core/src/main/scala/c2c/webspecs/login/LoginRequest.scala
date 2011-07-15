package c2c.webspecs
package login

import org.apache.http.auth.params.AuthPNames
import collection.JavaConverters._
import org.apache.http.client.params.{CookiePolicy, ClientPNames, HttpClientParams, AuthPolicy}
import org.apache.http.impl.client.{DefaultRedirectStrategy, DefaultHttpClient}
import org.apache.http.protocol.HttpContext
import org.apache.http.{HttpStatus, Header, HttpResponse, HttpRequest}
import java.net.URI
import org.apache.http.client.utils.URIUtils
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import java.lang.IllegalStateException

object LoginRequest {
  def apply(user:String,pass:String):Request[Any,Nothing] = Config.loadStrategy[Request[Any,Nothing]]("login") fold (
    throw _,
    strategy =>
      strategy.getConstructor(classOf[String],classOf[String]).newInstance(user,pass)
  )
}

trait LoginRequest extends Request[Any,Nothing] {
  def user:String
}
