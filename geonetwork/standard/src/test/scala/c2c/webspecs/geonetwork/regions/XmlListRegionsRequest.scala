package c2c.webspecs
package geonetwork.regions


case class XmlListRegionsRequest(maxRecords: Option[Int] = None, categoryId: Option[String] = None, label: Option[String] = None) 
    extends AbstractGetRequest("xml.regions.list", XmlRegionFactory,
        (maxRecords.toSeq.map(SP("maxRecords",_)) ++ 
            categoryId.toSeq.map(SP("categoryId",_)) ++ 
            label.toSeq.map(SP("label", _))) : _*) 

