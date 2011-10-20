/*
 * Created by IntelliJ IDEA.
 * User: jeichar
 * Date: 2/15/11
 * Time: 2:38 PM
 */
package c2c.webspecs;
trait UriResolver {
  def apply(service: String, params: Seq[(String,String)]):String
}

abstract class BasicServerResolver(autoAddPath:String) extends UriResolver {
  def apply(uri: String, params: Seq[(String, String)]) = {
    val baseURL = Properties.testServer

    val segments = uri.split("/")
    if(segments.size > 1){
      val sep = if (uri contains "?") "&" else "?"
      val paramString = params.map { e => e._1 + "=" + e._2 }.mkString("&")
      uri + sep + paramString
    } else {
      val serviceUrl = "http:/" :: baseURL :: autoAddPath :: uri :: Nil mkString "/"

      if (params.isEmpty) {
        serviceUrl
      } else {
        val paramString = params.map {
          case (key, value) => key + "=" + value
        } mkString ("?", "&", "")
        serviceUrl + paramString
      }
    }
  }
}