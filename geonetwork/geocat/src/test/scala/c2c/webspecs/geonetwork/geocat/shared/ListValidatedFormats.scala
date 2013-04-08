package c2c.webspecs
package geonetwork
package geocat
package shared

import c2c.webspecs.geonetwork.geocat.shared.SharedObjectTypes._

object ListValidatedFormats
  extends AbstractGetRequest[Any, List[SharedObject]](
    "format.admin!",
    SelfValueFactory[Any, List[SharedObject]]) 
  with BasicValueFactory[List[SharedObject]] {

  def createValue(rawValue: BasicHttpValue): List[SharedObject] = {
    val formats = rawValue.toXmlValue.getXml \ "gui" \ "formats" \ "record"

    formats.toList flatMap { format =>
      val validated = (format \ "validated").text.trim.toLowerCase()
      if (validated == "y") {
        val id = (format \ "id").text.trim
        val url = Some(s"local://xml.format.get?id=$id")
        val description = (format \ "name").text.trim + "(" + (format \ "version").text.trim + ")"
        val objType = SharedObjectTypes.formats

        Some(SharedObject(id, url, description, objType))
      } else {
        None
      }
    }
  }
}
