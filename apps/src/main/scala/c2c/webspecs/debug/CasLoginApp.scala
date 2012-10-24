
package c2c.webspecs
package debug
import c2c.webspecs.login.CasLogin
import c2c.webspecs.geonetwork.GeonetworkSpecification

object CasLoginApp extends WebspecsApp {
    new CasLogin("jeichar","jeichar").execute().value.withXml{xml =>
      println(xml)
    }
    val request = GetRequest("http://c2cpc55.camptocamp.com/geonetwork/srv/fr/main.home?login")
    val loginBanner = request.execute().value.withXml{xml =>
      // println(xml)
      xml \\ "form" filter {n => (n \\ "@name" text) contains "logout"}
    }
    println(loginBanner)
}