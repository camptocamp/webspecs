package c2c.webspecs
package geonetwork

import xml.NodeSeq

/**
 * User: jeichar
 * Date: 1/19/12
 * Time: 4:23 PM
 */

case class XmlSearch(maxRecords:Int, params:(Any, Any)*)
  extends AbstractGetRequest("q", XmlSearchResultFactory,
    (params ++ Seq('to -> maxRecords, 'fast -> 'index, 'hitsperpage -> maxRecords)).map(Param.stringMapping):_*)

object XmlSearchResultFactory extends BasicValueFactory[XmlSearchValues] {
  def createValue(rawValue: BasicHttpValue) = new XmlSearchValues(rawValue)
}

case class XmlSearchValues(rawValue:BasicHttpValue) {
  lazy val xml = XmlValueFactory.createValue(rawValue).getXml
  lazy val records = (xml \\ "metadata").toList map (new XmlSearchValue(_))
  lazy val summary = (xml \\ "summary").headOption
}
class XmlSearchValue(xml:NodeSeq) {
  override val toString = xml \\ "title" text
}