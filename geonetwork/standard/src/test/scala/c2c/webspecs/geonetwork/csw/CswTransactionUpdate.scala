package c2c.webspecs
package geonetwork
package csw

import scala.xml._

object CswTransactionUpdate {
  def apply(filter:Node, updatedMetadata:String):CswTransactionUpdate = CswTransactionUpdate(filter, XML.loadString(updatedMetadata))
  def apply(fileId:String, updatedMetadata:String):CswTransactionUpdate = CswTransactionUpdate(PropertyIsEqualTo("Identifier", fileId).xml, updatedMetadata)
  def apply(filter:OgcFilter, updatedMetadata:Node):CswTransactionUpdate = CswTransactionUpdate(filter.xml, updatedMetadata)
  def apply(fileId:String, updatedMetadata:Node):CswTransactionUpdate = CswTransactionUpdate(PropertyIsEqualTo("Identifier", fileId).xml, updatedMetadata)
}
case class CswTransactionUpdate(filter:Node, updatedMetadata:Node) extends AbstractXmlPostRequest("csw", XmlValueFactory) {
	val xmlData = 
<csw:Transaction service="CSW" version="2.0.2" xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" xmlns:ogc="http://www.opengis.net/ogc" 
    xmlns:apiso="http://www.opengis.net/cat/csw/apiso/1.0">
    <csw:Update>
        <csw:Constraint version="1.0.0">
			{filter}
        </csw:Constraint>
			{updatedMetadata}
    </csw:Update>
</csw:Transaction>

}