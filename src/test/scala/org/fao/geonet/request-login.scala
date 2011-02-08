package org.fao.geonet

import java.net.HttpURLConnection
import org.apache.commons.codec.binary.Base64.encodeBase64


object Login {
  def apply(user:String,pass:String) = {
    Config.property("login") match {
      case Some("basic") => BasicAuthLogin(user,pass)
      case _ => LoginService(user,pass)
    }
  }
}
case class LoginService(user:String, pass:String) extends GetRequest("user.login",XmlResponseFactory, "username" -> user, "password" -> pass) {
  override def toString() = "Login(%s,%s)".format(user,pass)
}
case class BasicAuthLogin(user:String, pass:String) extends Request {
  object BasicAuthSideEffect extends SideEffect {
    val authorization = "Basic " + new String(encodeBase64("%s:%s".format(user, pass).getBytes("UTF-8")))
    //val authorization = "Basic " + encodeBase64("%s:%s".format(user, pass).getBytes("US-ASCII"))

    def apply(conn: HttpURLConnection) = conn.addRequestProperty("Authorization", authorization)
  }

  def exec(sideEffect: Option[SideEffect]) = new EmptyResponse (this,BasicAuthSideEffect)

  override def toString() = "BasicAuthLogin(%s,%s)".format(user,pass)
}