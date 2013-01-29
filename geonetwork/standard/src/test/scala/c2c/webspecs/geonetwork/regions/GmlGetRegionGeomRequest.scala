package c2c.webspecs
package geonetwork.regions


case class GmlGetRegionGeomRequest(id: String)
    extends AbstractGetRequest[Any, XmlValue]("region.geom.gml3", XmlValueFactory, SP("id" -> id))

