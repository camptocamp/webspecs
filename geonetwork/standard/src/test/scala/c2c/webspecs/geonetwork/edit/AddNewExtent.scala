package c2c.webspecs
package geonetwork
package edit

import AddSites.ExtentAddSite

object AddNewExtent {
  def apply(addSite: ExtentAddSite = AddSites.extent) = { response: Response[EditValue] =>
      import XmlUtils._
      val nodeRef = XLink.lookupXlinkNodeRef(addSite.toString)(response.value)

      new AddNewExtent(lookupId(response.value), lookupVersion(response.value), nodeRef, addSite)
  }
}
class AddNewExtent(id: String, editVersion: String, nodeRef: String, addSite: ExtentAddSite)
  extends Add("metadata.elem.add", id, editVersion, nodeRef, addSite, "child" -> "")
