package c2c.webspecs
package geonetwork
package geocat
package shared

import c2c.webspecs.geonetwork.geocat.shared.SharedObjectTypes._

object ListValidatedContacts
  extends AbstractGetRequest[Any, List[SharedObject]](
      "validated.shared.user.admin!", 
      SelfValueFactory[Any, List[SharedObject]]
  ) with BasicValueFactory[List[SharedObject]] {
  def createValue(rawValue:BasicHttpValue):List[SharedObject] = {
    val users = rawValue.toXmlValue.getXml \ "response" \ "record"
    
    users.toList map {user =>
      val id = (user \ "id").text.trim
      val url = Some(s"local://xml.user.get?id=$id")
      val description = (user \ "username").text.trim 
      val objType = SharedObjectTypes.contacts
      
      SharedObject(id, url, description, objType)
    }
  }
}
