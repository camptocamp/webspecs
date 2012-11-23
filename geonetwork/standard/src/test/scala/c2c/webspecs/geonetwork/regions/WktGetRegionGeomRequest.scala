package c2c.webspecs
package geonetwork.regions


case class WktGetRegionGeomRequest(id: String)
    extends AbstractGetRequest[Any, String]("region.geom.wkt", SelfValueFactory(), SP("id" -> id))
    with BasicValueFactory[String] {
  def createValue(rawValue:BasicHttpValue) = rawValue.toTextValue.getText
}

