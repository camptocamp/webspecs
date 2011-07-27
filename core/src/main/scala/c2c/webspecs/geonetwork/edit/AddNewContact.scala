package c2c.webspecs
package geonetwork
package edit


import AddSites.ContactAddSite

object AddNewContact {
  def apply(addSite:ContactAddSite=AddSites.contact) = { response:Response[EditValue] =>
      import XmlUtils._
      val nodeRef = XLink.lookupXlinkNodeRef(addSite.toString)(response.value)

      new AddNewContact(lookupId(response.value), lookupVersion(response.value), nodeRef, addSite)
  }
}
class AddNewContact(id:String, editVersion:String, nodeRef:String, addSite:ContactAddSite)
  extends Add("metadata.elem.add",id,editVersion, nodeRef, addSite, "child" -> "")
