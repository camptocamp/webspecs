package c2c.webspecs
package geonetwork

/**
 * @param labels Labels with the key being the 2 letter langcode in upper case
 */
case class IsoKeyword(uri:String,thesaurus:String,labels:Map[String,String]) {
  def label(code:String) = labels(code.toUpperCase)
}
