package c2c.webspecs.geonetwork.geocat.shared

import c2c.webspecs.geonetwork.geocat.shared.SharedObjectTypes.SharedObjectType
import c2c.webspecs.XmlValueFactory
import c2c.webspecs.BasicValueFactory
import c2c.webspecs.BasicHttpValue

class SharedObjectListFactory(objType: SharedObjectType) extends BasicValueFactory[SharedObjectList] {
  override def createValue(rawValue: BasicHttpValue) = {
    val xmlValue = XmlValueFactory.createValue(rawValue)
    val list = xmlValue.withXml {
      xml =>
        (xml \\ "records" \\ "record").toList map {
          record =>
            val id = (record \\ "id" text)
            val url = {
              val urlTag = record \\ "url"
              if (urlTag.isEmpty) None
              else Some((record \\ "url").text.trim)
            }
            val desc = (record \\ "desc" text)
            SharedObject(id, url, desc, objType)
        }
    }
    new SharedObjectList(rawValue, list)

  }
}
