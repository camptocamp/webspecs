package c2c.webspecs
package geonetwork
package csw

import scala.xml._

object CswTransactionInsert {
  def apply(metadata:String):CswTransactionInsert = CswTransactionInsert(XML.loadString(metadata))
}
case class CswTransactionInsert(metadata:Node) extends AbstractXmlPostRequest("csw-publication", XmlValueFactory) {
	val xmlData = 
<csw:Transaction service="CSW" version="2.0.2" xmlns:csw="http://www.opengis.net/cat/csw/2.0.2">
    <csw:Insert>
			{metadata}
    </csw:Insert>
</csw:Transaction>

}