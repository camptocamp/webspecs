package c2c.webspecs
package geonetwork

import c2c.webspecs.login.LoginRequest

class LoginService(val user:String, pass:String)
  extends AbstractGetRequest[Any,XmlValue]("user.login",
      XmlValueFactory, 
      P("username" -> user), 
      P("password" -> pass)) 
   with LoginRequest {
  override def toString() = "Login(%s,xxxxxx)".format(user)
}
