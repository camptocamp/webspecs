package c2c.webspecs
package geonetwork

import c2c.webspecs.login.LoginRequest

class LoginService(val user:String, pass:String)
  extends AbstractFormPostRequest[Any,XmlValue]("/j_spring_security_check",
      XmlValueFactory, 
      P("username" -> user), 
      P("password" -> pass)) 
   with LoginRequest {
  override def toString() = "Login(%s,xxxxxx)".format(user)
}
