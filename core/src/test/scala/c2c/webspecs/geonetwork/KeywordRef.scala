package c2c.webspecs
package geonetwork
import java.net.URLEncoder

/**
 * Represents a Geonetwork keyword
 */
case class KeywordRef(id:String,value:String,definition:String,uri:String,thesaurus:List[String]) {
  val encodedURI = URLEncoder.encode(uri,"UTF-8")
  lazy val namespace = uri.split("#",2).head+"#"
  lazy val code = uri.split("#",2).last
  lazy val encodedNamespace = URLEncoder.encode(namespace,"UTF-8")
  lazy val encodedCode = URLEncoder.encode(code,"UTF-8")
}
