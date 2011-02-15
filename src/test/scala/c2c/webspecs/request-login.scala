package c2c.webspecs

import org.apache.http.auth.params.AuthPNames
import collection.JavaConverters._
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.params.{HttpClientParams, AuthPolicy}

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
class CasLogin(user:String, pass:String) extends Request[Any,Null] {
  def apply(in: Any)(implicit context: ExecutionContext) = {
    HttpClientParams.setRedirecting(context.httpClient.getParams, true)
    val PostLoginData = new AbstractFormPostRequest(Properties.get("casURL"),XmlValueFactory,"login" -> user, "password" -> pass) {
      override def request = {
        val request = super.request
        request.addHeader("referrer",uri)
        request
      }
    }
    val login = GetRequest("main.home","login" -> "") then PostLoginData
    login(None).value.withXml{xml => println(xml)}
    EmptyResponse
  }
}