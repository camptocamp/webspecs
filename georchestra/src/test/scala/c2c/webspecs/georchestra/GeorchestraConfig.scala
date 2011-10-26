package c2c.webspecs
package georchestra

import c2c.webspecs.Config

class GeorchestraConfig(specName:String) extends Config(specName) {
	val user = Properties("user").getOrElse(throw new IllegalStateException("Unable to find user property"))
    val password = Properties("password").getOrElse(throw new IllegalStateException("Unable to find password property"))
    
    lazy val login = c2c.webspecs.login.LoginRequest(user,password)
    lazy val logout = c2c.webspecs.login.LogoutRequest()
}