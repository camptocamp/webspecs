package c2c.webspecs
package geonetwork
package csw

object OutputSchemas {
  abstract class  OutputSchema (val name:String) {
    override def toString: String = name
  }
  case object Record extends OutputSchema("csw:Record")
  case object IsoRecord extends OutputSchema("csw:IsoRecord")
  val DublinCore = Record
}