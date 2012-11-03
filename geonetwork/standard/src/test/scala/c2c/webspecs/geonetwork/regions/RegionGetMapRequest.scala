package c2c.webspecs
package geonetwork.regions


case class RegionGetMapRequest(id: String, categoryId: String)
    extends AbstractGetRequest[Any, Array[Byte]]("region.getmap", SelfValueFactory(), SP("id" -> id), SP("categoryId" -> id))
    with BasicValueFactory[Array[Byte]] {
  def createValue(rawValue:BasicHttpValue) = rawValue.data.right.get
}

