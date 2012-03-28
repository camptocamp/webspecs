package c2c.webspecs
package geonetwork
package csw

import scala.xml._

object CswTransactionDelete {
  def apply(filter:OgcFilter):CswTransactionDelete = CswTransactionDelete(filter.xml)
  def apply(fileId:String):CswTransactionDelete = CswTransactionDelete(PropertyIsEqualTo("Identifier", fileId).xml)
}
case class CswTransactionDelete(filter:Node) extends AbstractXmlPostRequest("csw-publication", XmlValueFactory) {
	val xmlData = 
<csw:Transaction service="CSW" version="2.0.2" xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" xmlns:ogc="http://www.opengis.net/ogc" 
    xmlns:apiso="http://www.opengis.net/cat/csw/apiso/1.0">
    <csw:Delete>
        <csw:Constraint version="1.0.0">
			<ogc:Filter>
			{filter}
			</ogc:Filter>
        </csw:Constraint>
    </csw:Delete>
</csw:Transaction>

}