package c2c.webspecs
package geonetwork

import org.apache.http.auth.params.AuthPNames
import collection.JavaConverters._
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.params.{HttpClientParams, AuthPolicy}

class LoginService(user:String, pass:String)
  extends AbstractGetRequest[Any,XmlValue]("user.login",XmlValueFactory, P("username", user), P("password", pass)) {
  override def toString() = "Login(%s,%s)".format(user,pass)
}