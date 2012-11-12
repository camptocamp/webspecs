package c2c.webspecs
package geonetwork.regions


case class RegionGetMapRequest(id: String)
    extends AbstractGetRequest[Any, Array[Byte]]("region.getmap.pdf", SelfValueFactory(), SP("id" -> id))
    with BasicValueFactory[Array[Byte]] {
  def createValue(rawValue:BasicHttpValue) = rawValue.data.right.get
}

