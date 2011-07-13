package c2c.webspecs
package geonetwork
package edit

import AddSites.ExtentAddSite

object AddNewExtent {
  def apply(addSite: ExtentAddSite = AddSites.extent): (Response[XmlValue]) => AddNewExtent = { response: Response[XmlValue] =>
    response.value.withXml { md =>
      implicit val allInput = md \\ "input"
      val nodeRef = XLink.lookupXlinkNodeRef(addSite.toString)(md)
      import XmlUtils._
      new AddNewExtent(lookupId, lookupVersion, nodeRef, addSite)
    }
  }
}
class AddNewExtent(id: String, editVersion: String, nodeRef: String, addSite: ExtentAddSite)
  extends Add("metadata.elem.add", id, editVersion, nodeRef, addSite, "child" -> "")
