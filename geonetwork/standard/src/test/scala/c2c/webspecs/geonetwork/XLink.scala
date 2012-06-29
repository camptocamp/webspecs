package c2c.webspecs
package geonetwork

import xml.{NodeSeq, Node}
import edit.AddSites
import c2c.webspecs.geonetwork.edit.EditValue

object XLink {
  
  final val PROTOCOL = "local://"
    
  def apply(node:Node):XLink = {
    val url = hrefFrom(node).get
    val id = XmlUtils.extractId(url).get
    XLink(url, id, node)
  }
  def id(node:Node) = hrefFrom(node).flatMap(XmlUtils.extractId)
  def hrefFrom(node:Node) = node.attributes.asAttrMap.get("xlink:href")
  def findAll(xml:NodeSeq,site:AddSites.AddSite) = {
    val name = site.name
    val nodes = xml \\ name filter {n => hrefFrom(n).isDefined}
    nodes map apply
  }

  def lookupXlinkNodeRef(nodeName:String)(editVal:EditValue):String = {

    val md = (editVal.getXml \ "_").drop(1).head

    val xlinkSearchBoxLinks = md \\ "_" collect {
      case n if ((n \ "child" \@ "name") contains nodeName) => n \ "element" \@ "ref" head
    }
    xlinkSearchBoxLinks.headOption getOrElse {throw new IllegalArgumentException(nodeName+" does not have an xlink site in this metadata")}
  }

}

case class XLink(url:String,id:String,xml:Node) {
  lazy val nonValidated = xml.attributes.asAttrMap.get("xlink:role").exists{_ == "http://www.geonetwork.org/non_valid_obj" }
  lazy val isValidated = !nonValidated
  lazy val formatVersion = (xml \\ "version" text).trim
  lazy val formatName = (xml \\ "name" text).trim

  override def toString = "XLink(%s,%s".format(url,id)
}
