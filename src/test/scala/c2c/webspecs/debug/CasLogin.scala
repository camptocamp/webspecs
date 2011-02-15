package c2c.webspecs
package debug

object CasLogin extends Application {
  ExecutionContext.withDefault {context =>
    implicit val c = context
    val request = Login("jeichar","jeichar") then GetRequest("http://c2cpc55.camptocamp.com/geonetwork/srv/fr/main.home?login")
    val loginBanner = request(None).value.withXml{xml =>
      println(xml)
      xml \\ "td" filter {n => (n \\ "@class" text).trim == "banner-login"}
    }
    println(loginBanner)
  }
}