package c2c.webspecs
package login

import org.apache.http.client.params.HttpClientParams
import org.apache.http.client.params.CookiePolicy
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.client.DefaultRedirectStrategy
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.protocol.HttpContext
import org.apache.http.HttpStatus
import java.net.URI
import org.apache.http.client.utils.URIUtils

/**
 * Requires property casURL
 */
class CasLogin(val user:String, pass:String) extends Request[Any,XmlValue] with LoginRequest {
  def execute(in: Any)(implicit context: ExecutionContext, uriResolvers:UriResolver) = {

    // note redirecting is required for cas login to work and must be left on
    HttpClientParams.setRedirecting(context.httpClient.getParams, true)
    HttpClientParams.setCookiePolicy(context.httpClient.getParams(),CookiePolicy.BROWSER_COMPATIBILITY);

    // need to use a modified redirect strategy that will also redirect POST requests
    context.httpClient.asInstanceOf[DefaultHttpClient].setRedirectStrategy(new DefaultRedirectStrategy {
      override def isRedirected(request: HttpRequest, response: HttpResponse, context: HttpContext) = {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }

        val statusCode = response.getStatusLine().getStatusCode();
        val locationHeader = response.getFirstHeader("location");
        statusCode match {
          case HttpStatus.SC_MOVED_TEMPORARILY |
               HttpStatus.SC_MOVED_PERMANENTLY |
               HttpStatus.SC_TEMPORARY_REDIRECT => locationHeader != null
          case HttpStatus.SC_SEE_OTHER => true
          case _ => false;
        }
      }
    })

    def PostLoginData(action:URI, lt:String) = FormPostRequest(action.toString,
      "username" -> user,
      "password" -> pass,
      "lt" -> lt,
      "_eventId" -> "submit",
      "submit" -> "LOGIN"
    )

    val login = GetRequest("main.home","login" -> "") then { response:Response[XmlValue] =>
      response.value.withXml{ xmlData =>
        val lt = xmlData \\ "input" find {n => (n \\ "@name" text).trim == "lt"}
        val basicValue = response.basicValue
        val redirectURL = new URI(basicValue.finalHost)
        val actionPath = (xmlData \\ "form" \\ "@action").text.trim
        val action = URIUtils.createURI(redirectURL.getScheme,redirectURL.getHost,redirectURL.getPort, actionPath,null,null)
        val postRequest = lt map { node => PostLoginData(action,(node \\ "@value").text.trim) }

        postRequest getOrElse NoRequest
      }
    }

    val response = login.execute()
    //assert(response.basicValue.responseCode == 200, "Login failed. reponseCode = "+response.basicValue.responseCode)
    assert(GetRequest("config").execute().basicValue.responseCode != 403, "Login failed")

    response
  }
}