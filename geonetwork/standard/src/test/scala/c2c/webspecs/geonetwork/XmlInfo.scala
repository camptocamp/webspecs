package c2c.webspecs
package geonetwork

import scala.xml.Elem

/**
 * Perform a xmlInfo request for the given type and returns the records obtained from request as a list
 */
case class XmlInfo(infoType:String) 
    extends AbstractGetRequest[Any, List[Elem]]("xml.info", XmlInfoValueFactory, SP('type -> infoType))
    
object XmlInfoValueFactory extends BasicValueFactory[List[Elem]] {
  def createValue(rawValue: BasicHttpValue) = (rawValue.toXmlValue.getXml \ "_" \ "_").toList.collect{case n:Elem => n}
}