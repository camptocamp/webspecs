package c2c.webspecs
package geonetwork
package edit


import AddSites.ContactAddSite

object AddNewContact {
  def apply(addSite:ContactAddSite=AddSites.contact) = { response:Response[XmlValue] =>
    response.value.withXml { md =>
      implicit val allInput = md \\ "input"
      val nodeRef = XLink.lookupXlinkNodeRef(addSite.toString)(md)
      import XmlUtils._
      new AddNewContact(lookupId, lookupVersion, nodeRef, addSite)
    }
  }
}
class AddNewContact(id:String, editVersion:String, nodeRef:String, addSite:ContactAddSite)
  extends Add("metadata.elem.add",id,editVersion, nodeRef, addSite, "child" -> "")
