package c2c.webspecs
package geonetwork
package csw

import scala.xml._

object CswTransactionFullUpdate {
  def apply(updatedMetadata:String):CswTransactionFullUpdate = CswTransactionFullUpdate(XML.loadString(updatedMetadata))
}

case class CswTransactionFullUpdate(updatedMetadata:Node) extends AbstractXmlPostRequest("csw-publication", XmlValueFactory) {
	val xmlData = 
<csw:Transaction service="CSW" version="2.0.2" xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" xmlns:ogc="http://www.opengis.net/ogc" 
    xmlns:apiso="http://www.opengis.net/cat/csw/apiso/1.0">
    <csw:Update>
		{updatedMetadata}
    </csw:Update>
</csw:Transaction>
}