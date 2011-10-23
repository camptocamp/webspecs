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

abstract class BasicServerResolver(scheme:String, autoAddSegment:String) extends UriResolver {
  def apply( uri: String, params: Seq[(String, String)]) = {
    val baseURL = Properties.testServer

    val segments = uri.split("/")
    if(uri.startsWith("http:/") | uri.startsWith(scheme)){
      val sep = if (uri contains "?") "&" else "?"
      val paramString = params.map { e => e._1 + "=" + e._2 }.mkString("&")
      uri + sep + paramString
    } else {
      
      val serviceUrl = 
        if(segments contains autoAddSegment) (scheme+":/") :: baseURL :: uri :: Nil mkString "/"
        else (scheme+":/") :: baseURL :: autoAddSegment :: uri :: Nil mkString "/"

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