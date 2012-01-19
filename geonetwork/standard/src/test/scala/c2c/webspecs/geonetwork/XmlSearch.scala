package c2c.webspecs
package geonetwork

import xml.NodeSeq

/**
 * User: jeichar
 * Date: 1/19/12
 * Time: 4:23 PM
 */

case class XmlSearch(params:(Any, Any)*) extends AbstractGetRequest("q", XmlValueFactory, params.map(Param.stringMapping):_*)
                             /*
object XmlSearchResultFactory extends BasicValueFactory[List[XmlSearchValue]] {
  
}

case class XmlSearchValues(xml:NodeSeq) {
  val records =
}
case class XmlSearchValue(xml)    */