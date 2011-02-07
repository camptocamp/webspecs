package org.fao.geonet


object DeleteTestData extends Application {

  import Config._

  val mdSearchRequest = (adminLogin then usersList) {
    response =>
      val users = findUserIds(response)(_ contains userPrefix) map {uid => PropertyIsLike("_owner",uid)}

      if(users.isEmpty) {
        NoRequest
      } else {
        XmlRequest("csw",mdSearchXml(users))
      }
  }

  val DeleteMds = (Config.adminLogin then mdSearchRequest) {
    case response:EmptyResponse => NoRequest
    case response =>
      val ids = response.xml match {
        case Right(xml) => xml \\ "info" \ "id"
        case Left(error) => throw error
  }
      ((NoRequest:Request) /: ids){case (req, id) => req then Get("metadata.delete", "id" -> id.text)}
  }

  val DeleteGroups = findGroups(_ contains userPrefix) {
    groupIds => ((NoRequest:Request) /: groupIds){case (req,gid) =>
      req then Get("group.remove", "id" -> gid, "users" -> "delete")}
  }

  val DeleteSharedContacts = DeleteCreated(SharedObjectTypes.contacts,Nil)
  val DeleteNewExtents = DeleteCreated(SharedObjectTypes.extents,Nil)
  val DeleteNewFormats = DeleteCreated(SharedObjectTypes.formats,Nil)
  val DeleteNewKeywords = DeleteCreated(SharedObjectTypes.keywords,Nil)
  val DeleteNewDeleted = DeleteCreated(SharedObjectTypes.deleted,Nil)

  (Config.adminLogin then DeleteMds then DeleteGroups then DeleteTestFormats() then DeleteNewFormats then DeleteSharedContacts
      then DeleteNewExtents then DeleteNewKeywords then DeleteNewDeleted) {response =>
    require(response.responseCode == 200, "Obtained a "+response.responseCode+" responseCode when deleting group")
  }
}
