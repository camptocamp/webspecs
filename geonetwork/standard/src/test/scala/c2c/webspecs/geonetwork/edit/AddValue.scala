package c2c.webspecs
package geonetwork
package edit
import scala.xml.Node
import scala.xml.NodeSeq

class AddValue(val basicValue:BasicHttpValue,
               val newElement:Node,
               val newXml:NodeSeq ) extends EditValue {
  lazy val editVal = EditValueFactory(basicValue)
  lazy val id = editVal.id
  lazy val version = editVal.version
  lazy val href = XLink hrefFrom newElement
}