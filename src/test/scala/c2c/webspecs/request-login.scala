package c2c.webspecs

import org.apache.http.auth.params.AuthPNames
import collection.JavaConverters._
import org.apache.http.client.params.{CookiePolicy, ClientPNames, HttpClientParams, AuthPolicy}

object Login {
  def apply(user:String,pass:String):Request[Any,Any] = Config.loadStrategy[Request[Any,Any]]("login") fold (
    throw _,
    strategy =>
      strategy.getConstructor(classOf[String],classOf[String]).newInstance(user,pass)
  )
}
class BasicAuthLogin(user:String, pass:String) extends Request[Any,Null] {
  def apply (in: Any)(implicit context:ExecutionContext) = {
    import AuthPolicy._
    context.httpClient.getParams.setParameter(AuthPNames.TARGET_AUTH_PREF, BASIC :: Nil asJava);
    EmptyResponse
  }

  override def toString() = "BasicAuthLogin(%s,%s)".format(user,pass)
}

/**
 * Requires property casURL
 */
class CasLogin(user:String, pass:String) extends Request[Any,XmlValue] {
  def apply(in: Any)(implicit context: ExecutionContext) = {
    HttpClientParams.setRedirecting(context.httpClient.getParams, true)
    context.httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
    def PostLoginData(lt:String) = new AbstractFormPostRequest(Properties.get("casURL"),
      XmlValueFactory,
      "username" -> user,
      "password" -> pass,
      "lt" -> lt,
      "_eventId" -> "submit",
      "submit" -> "LOGIN"
    ) {
      override def request(in:Any) = {
        val request = super.request(in)
        request.addHeader("Referer",uri)
        request
      }
    }
    val login = GetRequest("main.home","login" -> "") then { _.value.withXml{
      xmlData =>
        val lt = xmlData \\ "input" find {n => (n \\ "@name" text).trim == "lt"}
        val postRequest = lt map { node => PostLoginData((node \\ "@value" text).trim) }
        postRequest getOrElse NoRequest
      }
    }

    login(None)
  }
}