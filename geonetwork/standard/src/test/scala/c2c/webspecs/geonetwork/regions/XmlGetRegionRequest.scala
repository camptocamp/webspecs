package c2c.webspecs
package geonetwork.regions


case class XmlGetRegionRequest(id: String, categoryId: String)
    extends AbstractGetRequest("region.get.xml", XmlRegionFactory, SP("id" -> id), SP("categoryId" -> id))

