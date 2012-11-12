package c2c.webspecs
package geonetwork.regions


case class WktGetRegionGeomRequest(id: String)
    extends AbstractGetRequest[Any, String]("wkt.region.geom", SelfValueFactory(), SP("id" -> id))
    with BasicValueFactory[String] {
  def createValue(rawValue:BasicHttpValue) = rawValue.toTextValue.getText
}

