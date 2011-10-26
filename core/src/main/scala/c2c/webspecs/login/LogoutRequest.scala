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

object LogoutRequest {
  def apply():Request[Any,Any] = Config.loadStrategy[Request[Any,Any]]("logout") fold (
    throw _,
    strategy =>
      strategy.newInstance()
  )
}

trait LogoutRequest extends Request[Any,Any] 
