package c2c.webspecs

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

object Login {
  def apply(user:String,pass:String):Request[Any,Nothing] = Config.loadStrategy[Request[Any,Nothing]]("login") fold (
    throw _,
    strategy =>
      strategy.getConstructor(classOf[String],classOf[String]).newInstance(user,pass)
  )
}

class GeonetworkLoginService(user:String, pass:String) extends GetRequest("user.login", "username" -> user, "password" -> pass)

class BasicAuthLogin(user:String, pass:String) extends Request[Any,Null] {
  def apply (in: Any)(implicit context:ExecutionContext) = {
    import AuthPolicy._
    context.httpClient match {
      case client:DefaultHttpClient =>
        client.getParams.setParameter(AuthPNames.TARGET_AUTH_PREF, BASIC :: DIGEST :: Nil asJava)
        val cred = new UsernamePasswordCredentials(user,pass)
        client.getCredentialsProvider.setCredentials(AuthScope.ANY,cred)
      case _ =>
        throw new IllegalStateException(getClass.getSimpleName+" does not apply to "+context.httpClient.getClass.getName)
    }

    EmptyResponse
  }

  override def toString() = "BasicAuthLogin(%s,%s)".format(user,pass)
}

/**
 * Requires property casURL
 */
class CasLogin(user:String, pass:String) extends Request[Any,XmlValue] {
  def apply(in: Any)(implicit context: ExecutionContext) = {

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

    val response = login(None)
    //assert(response.basicValue.responseCode == 200, "Login failed. reponseCode = "+response.basicValue.responseCode)
    assert(GetRequest("config")(None).basicValue.responseCode != 403, "Login failed")

    response
  }
}