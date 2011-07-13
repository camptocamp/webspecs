package c2c.webspecs
package login

import collection.JavaConverters._
import org.apache.http.client.params.AuthPolicy
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.auth.params.AuthPNames
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.auth.AuthScope

class BasicAuthLogin(val user:String, pass:String) extends Request[Any,Null] with LoginRequest{
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

