package c2c.webspecs
package geonetwork

/**
 * Represents a Geonetwork keyword
 */
case class KeywordRef(id:String,lang:String, value:String,definition:String,uri:String,thesaurus:String) {
  lazy val namespace = uri.split("#",2).head+"#"
  lazy val code = uri.split("#",2).last
}
