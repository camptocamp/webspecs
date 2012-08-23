package c2c.webspecs
package geoserver
import scala.xml._

class GetFeatureRequest(typename:String, filter:NodeSeq, properties: String*) 
extends AbstractXmlPostRequest[Any,XmlValue]("geoserver/ows",XmlValueFactory) {
  
  val completeFilter = if(filter.nonEmpty) <fes:Filter>{filter}</fes:Filter> else NodeSeq.Empty
  val xmlData = 
<GetFeature service="WFS" version="2.0.0"
  		xmlns="http://www.opengis.net/wfs/2.0" 
  		xmlns:au="urn:x-inspire:specification:gmlas:AdministrativeUnits:3.0" 
  		xmlns:fes="http://www.opengis.net/fes/2.0" 
  		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
  		xsi:schemaLocation="http://www.opengis.net/wfs/2.0 http://schemas.opengis.net/wfs/2.0/wfs.xsd">
  <Query typeNames={typename}>
    {properties.map(prop => <PropertyName>{prop}</PropertyName>)}
    {completeFilter}
  </Query>
</GetFeature>
}