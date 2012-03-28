package c2c.webspecs
package geonetwork
package csw

import scala.xml._

object CswTransactionUpdate {
  def apply(fileId:String, updateFields:(String, String)*):CswTransactionUpdate = CswTransactionUpdate(PropertyIsEqualTo("Identifier", fileId).xml, updateFields:_*)
  def apply(filter:OgcFilter, updateFields:(String, String)*):CswTransactionUpdate = CswTransactionUpdate(filter.xml, updateFields:_*)
}
case class CswTransactionUpdate(filter:Node, updateFields:(String, String)*) extends AbstractXmlPostRequest("csw-publication", XmlValueFactory) {
	val xmlData = 
<csw:Transaction service="CSW" version="2.0.2" xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" xmlns:ogc="http://www.opengis.net/ogc" 
    xmlns:apiso="http://www.opengis.net/cat/csw/apiso/1.0">
    <csw:Update>
        <csw:Constraint version="1.0.0">
			<ogc:Filter>
				{filter}
			</ogc:Filter>
        </csw:Constraint>
		{for(field <- updateFields) yield {
			<csw:RecordProperty>
				<csw:Name>{field._1}</csw:Name>
				<csw:Value>{field._2}</csw:Value>
			</csw:RecordProperty>}}
    </csw:Update>
</csw:Transaction>

}
