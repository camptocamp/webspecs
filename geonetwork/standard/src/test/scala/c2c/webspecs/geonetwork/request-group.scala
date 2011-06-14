package c2c.webspecs
package geonetwork

import org.apache.http.entity.mime.content._
import scalax.io.Codec

object Group {
  def apply(name:String, description:String="",email:String="") = {
    new Group(name,description, email)
  }
}
class Group(val name:String, val description:String="",val email:String="") {
  def formParams = List(
    P("name",new StringBody(name,Codec.UTF8.charSet)),
    P("description",new StringBody(description,Codec.UTF8.charSet)),
    P("email",new StringBody(email,Codec.UTF8.charSet))
  )

  override def toString: String = "Group(%s,%s,%s)".format(name,description,email)
}

class GroupValue(val id:String, name:String, description:String="",email:String="")
  extends Group(name,description,email)
  with Id {
  override def toString: String = "Group(%s,%s,%s,%s)".format(id,name,description,email)
}

case object GroupValueListFactory extends BasicValueFactory[List[GroupValue]] {
   def createValue(rawValue: BasicHttpValue) = {
    rawValue.toXmlValue.withXml{ data =>
      (data \\ "record").toList map {
        groupRecord=>
          if(groupRecord.isEmpty) Log(Log.Error, "An group record must be found, responseCode was: "+rawValue.responseCode)
          val id = (groupRecord \ "id").text
          val name = (groupRecord \ "name").text
          val email = (groupRecord \ "description").text
          val description = (groupRecord \ "email").text
          new GroupValue(id,name,description,email)
      }
    }
  }
}
case class CreateGroup(group:Group)
  extends MultiPartFormRequest[Any,GroupValue](
    "group.update!",
    SelfValueFactory(),
    group.formParams:_*
  ) with BasicValueFactory[GroupValue] {
  def createValue(rawValue: BasicHttpValue) = {
    rawValue.toXmlValue.withXml{ data =>
      val groupRecord = data \\ "record" filter {rec => (rec \\ "name").text.trim == group.name}
      val id = (groupRecord \ "id").text
      if(id.isEmpty) Log(Log.Error, "An group id must be found, responseCode was: "+rawValue.responseCode)
      new GroupValue(id,group.name,group.description,group.email)
    }
  }
}

case class DeleteGroup(groupId:String,deleteUsers:Boolean)
  extends AbstractGetRequest(
    "group.remove",
    XmlValueFactory,
    P("id", groupId),
    P("users", if(deleteUsers) "delete" else "")
  )

case object ListGroups
  extends AbstractGetRequest(
    "group.list!",
    GroupValueListFactory
  )


case object GetUserGroups
  extends AbstractGetRequest[UserRef,List[GroupValue]](
    "xml.usergroups.list",
    GroupValueListFactory,
    InP[UserRef,String]("id",ref => ref.userId)
  ) {
  def setIn(id:String):Request[Any,List[GroupValue]] = setIn(new UserRef{val userId=id})
}