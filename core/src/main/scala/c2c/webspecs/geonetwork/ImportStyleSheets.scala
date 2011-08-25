package c2c.webspecs
package geonetwork


object ImportStyleSheets {
  abstract class ImportStyleSheet(val name:String) {
    override def toString: String = name
  }
  case object ISO_TO_CHE extends ImportStyleSheet("ISO19139-to-ISO19139CHE.xsl")
  case object GM03_V1 extends ImportStyleSheet("GM03-to-ISO19139CHE.xsl")  
  case object GM03_V2 extends ImportStyleSheet("GM03_2-to-ISO19139CHE.xsl")
  case object NONE extends ImportStyleSheet("_none_")
}