package org.fao.geonet

import java.util.UUID
import Config.TEST_TAG

case class Login(user:String, pass:String) extends GetRequest("user.login",XmlResponseFactory, "username" -> user, "password" -> pass) {
  override def toString() = "Login(%s,%s)".format(user,pass)
}
