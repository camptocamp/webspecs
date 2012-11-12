package c2c.webspecs
package geonetwork.regions


case class XmlGetRegionRequest(id: String)
    extends AbstractGetRequest[Any, Option[Region]]("xml.region.get", SelfValueFactory(), SP("id" -> id)) with BasicValueFactory[Option[Region]] {
  def createValue(rawValue:BasicHttpValue) = XmlRegionFactory.createValue(rawValue).headOption
}

