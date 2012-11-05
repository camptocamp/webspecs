package c2c.webspecs
package geonetwork.regions


case class XmlGetRegionRequest(id: String, categoryId: String)
    extends AbstractGetRequest("xml.region.get", XmlRegionFactory, SP("id" -> id), SP("categoryId" -> id))

