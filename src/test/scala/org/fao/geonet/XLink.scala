package org.fao.geonet

import xml.{NodeSeq, Node}

object XLink {
  def apply(node:Node):XLink = {
    val url = hrefFrom(node).get
    val id = Config.extractId(url).get
    XLink(url, id, node)
  }
  def id(node:Node) = hrefFrom(node).flatMap(Config.extractId)
  def hrefFrom(node:Node) = node.attributes.asAttrMap.get("xlink:href")
  def findAll(xml:NodeSeq,site:AddSites.AddSite) = {
    val nodes = xml \\ site.name filter {n => hrefFrom(n).isDefined}
    nodes map apply
  }
}

case class XLink(url:String,id:String,xml:Node) {
  lazy val nonValidated = xml.attributes.asAttrMap.get("xlink:role").exists{_ == "http://www.geonetwork.org/non_valid_obj" }
  lazy val isValidated = !nonValidated
  lazy val formatVersion = (xml \\ "version" text).trim
  lazy val formatName = (xml \\ "name" text).trim

  override def toString = "XList(%s,%s".format(url,id)
}
