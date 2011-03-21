package c2c.webspecs
package geonetwork

class GeonetworkURIResolver extends UriResolver{
  def apply(service: String, params: Seq[(String,String)]) = {
    if(service.contains("/")) {
      val sep = if(service contains "?") "&" else "?"
      val paramString = params.map{e=>e._1+"="+e._2}.mkString("&")
      service+sep+paramString
    } else {
      val baseURL = Properties(Properties.TEST_URL_KEY) getOrElse "localhost:8080/geonetwork/srv/"
      val locale = Properties(GeonetConfig.LOCALE_KEY) getOrElse "eng"
      val serviceUrl = "http:/" :: baseURL :: "geonetwork/srv" :: locale :: service :: Nil mkString "/"

      if (params.isEmpty) {
        serviceUrl
      } else {
        val paramString = params.map{
          case (key, value) => key + "=" + value
        } mkString ("?", "&", "")
        serviceUrl + paramString
      }
    }
  }
}