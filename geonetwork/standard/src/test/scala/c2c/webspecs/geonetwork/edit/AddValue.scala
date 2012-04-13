package c2c.webspecs
package geonetwork
package edit
import scala.xml.Node

class AddValue(val editVal:EditValue,
			   val basicValue:BasicHttpValue,
               val newElement:Node) extends EditValue {

  override lazy val text = editVal.text
  override lazy val xml = editVal.xml
  override lazy val html = editVal.html
  lazy val id = editVal.id
  lazy val version = editVal.version
  lazy val href = XLink hrefFrom newElement
}