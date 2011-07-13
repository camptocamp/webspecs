package c2c.webspecs
package geonetwork
import java.net.URLEncoder

/**
 * Represents a Geonetwork keyword
 */
case class KeywordRef(id:String,value:String,definition:String,uri:String,thesaurus:List[String]) {
  val encodedURI = URLEncoder.encode(uri,"UTF-8")
}
