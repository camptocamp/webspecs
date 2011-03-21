package c2c.webspecs
package debug

object CasLogin extends Application {
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