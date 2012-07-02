package c2c.webspecs.geonetwork
package geocat
import csw.OutputSchemas.OutputSchema

object OutputSchemas {
  case object CheIsoRecord extends OutputSchema("http://www.geocat.ch/2008/che")
  case object GM03UrlRecord extends OutputSchema("http://www.isotc211.org/2008/gm03_2")
  case object GM03_2Record extends OutputSchema("GM03_2Record")
  case object OwnRecord extends OutputSchema("own")
}