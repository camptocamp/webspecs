package c2c.webspecs
package geonetwork
package geocat

import edit._
import AddSites.{ AddSite, ExtentAddSite }
import XmlUtils.{ lookupId, lookupVersion }
import AddSites._
import scala.xml.Node

object StandardSharedExtents {
  abstract class StandardSharedExtent(val typeName: String, val id: String)
  case object KantonBern extends StandardSharedExtent("gn:kantoneBB", "2")
  case class Custom(_typeName:String, _id:String) extends StandardSharedExtent(_typeName,_id)
}

abstract class revisionExtent extends ExtentAddSite("che")

case class AddExtentXLink(extent: StandardSharedExtents.StandardSharedExtent,
  withPolygon: Boolean,
  addSite: ExtentAddSite = AddSites.extent)
  extends AddXLinkRequest(
    _ => "local://xml.extent.get?wfs=default&format=" + (if (withPolygon) "gmd_complete" else "gmd_bbox") + "&typename=" + extent.typeName + "&id=" + extent.id + "&extentTypeCode=true",
    (e: EditValue) => XLink.lookupXlinkNodeRef(addSite.name)(e),
    addSite)

case class AddContactXLink(contactId: Id, addSite: ContactAddSite)
  extends AddXLinkRequest(
    _ => "local://xml.user.get?" + "id=" + contactId + "&schema=iso19139.che&role=originator",
    (e: EditValue) => XLink.lookupXlinkNodeRef(addSite.name)(e),
    addSite)

case class AddFormatXLink(formatId: Id, addSite: FormatAddSite)
  extends AddXLinkRequest(
    _ => "local://xml.format.get?" + "id=" + formatId,
    (e: EditValue) => XLink.lookupXlinkNodeRef(addSite.name)(e),
    addSite)
case class AddKeywordXLink(thesaurus: String, namespace: String, id: String, addSite: KeywordAddSite)
  extends AddXLinkRequest(
    _ => "local://che.keyword.get?thesaurus=" + thesaurus + "&id=" + (namespace + id).encode + "&locales=en,it,de,fr",
    (e: EditValue) => XLink.lookupXlinkNodeRef(addSite.name)(e),
    addSite)

class AddXLinkRequest(xlink: EditValue => String, nodeRef: EditValue => String, addSite: AddSite)
  extends AbstractAddRequest("metadata.xlink.add!", nodeRef, addSite, InP("href", xlink)) {
  def extractElementXmlFromResponse(responseXml: Node) = (responseXml \ "_").drop(1).head
}
