package c2c.webspecs
package geonetwork

case class ListMetadataPrivileges(id: Id) extends AbstractGetRequest[Any, List[MetadataPrivilege]](
  "metadata.admin.form!", SelfValueFactory(),
  SP('id -> id.id)) with BasicValueFactory[List[MetadataPrivilege]] {
  def createValue(rawValue: BasicHttpValue): List[MetadataPrivilege] = {
    val response = rawValue.toXmlValue.getXml \ "response"
    val ops = (response \ "operations" map { op =>
      val id = (op \ "id").text.trim.toInt
      val name = (op \ "name").text.trim.toUpperCase
      id -> MetadataOperation(name, id)
    }).toMap

    val all = (response \ "groups" \ "group") flatMap { group =>
      val groupId = (group \ "id").text
      val opTypes = (group \ "oper") flatMap { op =>
        if ((op \ "on").nonEmpty) {
          ops.get((op \ "id").text.trim.toInt)
        } else {
          Nil
        }
      }
      
      opTypes map {new MetadataPrivilege(groupId, _)}
    }
    
    all.toList
  }
}