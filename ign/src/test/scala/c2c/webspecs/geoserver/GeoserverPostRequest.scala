package c2c.webspecs
package geoserver
import scala.xml.Elem
import scala.xml.XML

class GetFeatureRequest(typename:String, filter:Elem) 
extends AbstractXmlPostRequest[Any,XmlValue]("geoserver/ows",XmlValueFactory) {
  
  val xmlData = 
    <wfs:GetFeature service="WFS" version="2.0.0"
  xmlns:wfs="http://www.opengis.net/wfs"
  xmlns:fes="http://www.opengis.net/fes/2.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <wfs:Query typeNames={typename}>
    <fes:Filter>
{filter}
    </fes:Filter>
    </wfs:Query>
</wfs:GetFeature>
}