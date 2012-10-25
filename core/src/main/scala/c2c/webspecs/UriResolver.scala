/*
 * Created by IntelliJ IDEA.
 * User: jeichar
 * Date: 2/15/11
 * Time: 2:38 PM
 */
package c2c.webspecs;
trait UriResolver {
  def paramsToString(params: Seq[(String, String)], prefix:String) = if(params.isEmpty){
    ""
  } else {
    params.map { e => e._1 + "=" + e._2 }.mkString(prefix,"&","")
  }
  def apply(service: String, params: Seq[(String,String)]):String
}

object IdentityUriResolver extends UriResolver {
  def apply(service: String, params: Seq[(String,String)]):String = 
    service + paramsToString(params, "?")
}

abstract class BasicServerResolver(scheme:String, autoAddSegment:String) extends UriResolver {
  def baseServer = Properties.testServer
  def apply( uri: String, params: Seq[(String, String)]) = {

    val segments = uri.split("/")
    if(uri.startsWith("http:/") | uri.startsWith(scheme)){
      val sep = if (uri contains "?") "&" else "?"
      uri + paramsToString(params, sep)
    } else {
      
      val serviceUrlParts = 
        if(segments contains autoAddSegment) (scheme+":/") :: baseServer :: uri :: Nil
        else (scheme+":/") :: baseServer :: autoAddSegment :: uri :: Nil

      val serviceUrl = serviceUrlParts.filterNot(_.isEmpty()).mkString("/")
      if (params.isEmpty) {
        serviceUrl
      } else {
        serviceUrl + paramsToString(params, "?")
      }
    }
  }
}