package c2c.webspecs
package debug

import c2c.webspecs.geonetwork.GeonetworkSpecification

object GetFeatureApp extends WebspecsApp{
	  val xmlData = <wfs:GetFeature xmlns:wfs="http://www.opengis.net/wfs" service="WFS" version="1.0.0" xsi:schemaLocation="http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-transaction.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"><wfs:Query typeName="feature:communes" xmlns:feature="http://pigma.org/pigma_loc"><ogc:PropertyName xmlns:ogc="http://www.opengis.net/ogc">nom_commun</ogc:PropertyName><ogc:PropertyName xmlns:ogc="http://www.opengis.net/ogc">the_geom</ogc:PropertyName><ogc:Filter xmlns:ogc="http://www.opengis.net/ogc"><ogc:PropertyIsLike wildCard="*" singleChar="." escape="!"><ogc:PropertyName>nom_commun</ogc:PropertyName><ogc:Literal>*BEGLE*</ogc:Literal></ogc:PropertyIsLike></ogc:Filter></wfs:Query></wfs:GetFeature>
	  val response = new XmlPostRequest("http://ns383242.ovh.net/geoserver//wfs",xmlData).execute()
	  println(response.value.getText)
}