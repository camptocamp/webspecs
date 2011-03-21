package c2c.webspecs
package geonetwork

import xml.{NodeSeq, Node}

object XLink {
  def apply(node:Node):XLink = {
    val url = hrefFrom(node).get
    val id = XmlUtils.extractId(url).get
    XLink(url, id, node)
  }
  def id(node:Node) = hrefFrom(node).flatMap(XmlUtils.extractId)
  def hrefFrom(node:Node) = node.attributes.asAttrMap.get("xlink:href")
  def findAll(xml:NodeSeq,site:AddSites.AddSite) = {
    val nodes = xml \\ site.name filter {n => hrefFrom(n).isDefined}
    nodes map apply
  }

  def xlinkIdExtractor(nodeName:String) = {
    import java.util.regex.Pattern.quote
    (quote("javascript:displayXLinkSearchBox('/geonetwork/srv/")+".?.?.?"+quote("/metadata.xlink.add',") +
      "(.+?)"+quote(",'"+nodeName+"','")+".+?"+quote("');")).r
  }
  def lookupXlinkNodeRef(nodeName:String)(md:NodeSeq) = {
    val IdExtractor = xlinkIdExtractor(nodeName)

    val xlinkSearchBoxLinks = md \\ "a" \\ "@href" map {_.text} collect {
      case IdExtractor(id) => id
    }
    xlinkSearchBoxLinks.headOption getOrElse {throw new IllegalArgumentException(nodeName+" does not have an xlink site in this metadata")}
  }

}

case class XLink(url:String,id:String,xml:Node) {
  lazy val nonValidated = xml.attributes.asAttrMap.get("xlink:role").exists{_ == "http://www.geonetwork.org/non_valid_obj" }
  lazy val isValidated = !nonValidated
  lazy val formatVersion = (xml \\ "version" text).trim
  lazy val formatName = (xml \\ "name" text).trim

  override def toString = "XList(%s,%s".format(url,id)
}
