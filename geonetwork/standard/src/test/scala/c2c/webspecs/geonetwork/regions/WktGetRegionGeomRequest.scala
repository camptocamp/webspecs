package c2c.webspecs
package geonetwork.regions


case class WktGetRegionGeomRequest(id: String, categoryId: String)
    extends AbstractGetRequest[Any, String]("wkt.region.geom", SelfValueFactory(), SP("id" -> id), SP("categoryId" -> id))
    with BasicValueFactory[String] {
  def createValue(rawValue:BasicHttpValue) = rawValue.toTextValue.getText
}

