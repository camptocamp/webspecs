
package c2c.webspecs
package debug
import c2c.webspecs.login.CasLogin

object CasLoginApp extends App {
  ExecutionContext.withDefault {context =>
    implicit val c = context
    new CasLogin("jeichar","jeichar")(None).value.withXml{xml =>
      println(xml)
    }
    val request = GetRequest("http://c2cpc55.camptocamp.com/geonetwork/srv/fr/main.home?login")
    val loginBanner = request(None).value.withXml{xml =>
      // println(xml)
      xml \\ "form" filter {n => (n \\ "@name" text) contains "logout"}
    }
    println(loginBanner)
  }
}