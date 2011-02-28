package c2c.webspecs
package geonetwork

import java.util.UUID
import Properties.TEST_TAG

object UserProfiles extends Enumeration {
  type UserProfile = Value
  val Shared,Editor,UserAdmin,Reviewer,AdminRegisteredUser=Value
}

case class User(username:String=(TEST_TAG+UUID.randomUUID.toString.takeRight(5)),
                password:String=TEST_TAG,
                surname:String=TEST_TAG,
                name:String=TEST_TAG,
                email:String=TEST_TAG+"@camptocamp.com",
                position:LocalisedString=LocalisedString(TEST_TAG),
                profile:UserProfiles.UserProfile=UserProfiles.Shared,
                groups:Traversable[String]=Nil) {
  require(profile == UserProfiles.Shared && groups.nonEmpty || profile != UserProfiles,
     "Shared users are not part of groups: profile="+profile+", groups="+(groups mkString ","))

  def formParams():List[(String,String)] = formParams(None)
  def formParams(id:Int):List[(String,String)] = formParams(Some(id))
  def formParams(id: Option[Int]): List[(String, String)] = ("username" -> username ::
    "password" -> password ::
    "password2" -> password ::
    "name" -> name ::
    "surname" -> surname ::
    "profile" -> profile.toString ::
    position.formParams("position")) :::
    (if (groups.isEmpty) Nil else List("groups" -> (groups mkString ",")))
}

case class UserListValue(user:User, basicValue:BasicHttpValue,executionContext:ExecutionContext) extends XmlValue {
  lazy val id:Option[Int] = withXml{
    xml =>
      val tables = xml \\ "table"
      val correctTable = tables filter {_ \ "tr" \ "th" nonEmpty}
      val row = correctTable \ "tr" find { n =>
        (n \ "td" headOption).exists {_.text.trim == user.username}
      }
      row.get \\ "@onclick" find {_.text contains "deleteUser"} flatMap {onclick => XmlUtils.extractId(onclick.text) map {_.toInt}}
  }

  lazy val updatedUser:Option[User] = id match {
    case Some(userId) =>
      val response = GetUser(userId)(None)(executionContext)
      Some(response.value.user)
    case None => None
  }
}

case class GetUserValue(userId:Int, basicValue:BasicHttpValue) extends XmlValue {
  lazy val user = withXml { xml =>
    // TODO need another request to get username and profile
    val surname = (xml \\ "surname").text.trim
    val name = (xml \\ "name").text.trim
    val email = (xml \\ "email").text.trim
    def localizedLookup(tagName:String)= {
      val tag = xml \\ tagName
      def translation(langCode:String) = {
        val t = tag \\ "LocalisedCharacterString" find {n => (n \\ "@locale" text) == langCode.toUpperCase}
        t.map(t => langCode.toUpperCase -> t.text).toList
      }
      val en = translation("EN")
      val de = translation("DE")
      val fr = translation("FR")
      val it = translation("IT")
      LocalisedString(Map(en ::: de ::: fr ::: fr ::: it :_*))
    }
    val position = localizedLookup("positionName")
    User("","",surname,name,email,position)
  }
}
case class GetUser(userId:Int)
  extends AbstractGetRequest[Any,GetUserValue](
    "xml.user.get",
    SelfValueFactory(),
    "id" -> userId.toString,
    "schema" -> "iso19139.che",
    "role" -> "createValue") with BasicValueFactory[GetUserValue] {
  override def createValue(rawValue: BasicHttpValue) = new GetUserValue(userId,rawValue)
}

case class CreateUser(user:User)
  extends AbstractFormPostRequest(
    "user.update",
    SelfValueFactory(),
    user.formParams:_*)
  with ValueFactory[Any,UserListValue] {

  override def createValue[A <: Any, B >: UserListValue](request: Request[A, B], in: Any, rawValue: BasicHttpValue,executionContext:ExecutionContext) = {
    new UserListValue(user,rawValue,executionContext)
  }
}
object DeleteUser {
def apply() = (response:Response[IdValue]) => new DeleteUser(response.value.id)
}
case class DeleteUser(userId:String) extends AbstractGetRequest("user.remove", ExplicitIdValueFactory(userId), "id" -> userId)

object UpdateUser {
  def apply(id:Int, user:User) = new UpdateUser(id,user)
  def apply(user:User) = (res:Response[UserListValue]) => new UpdateUser(res.value.id.get,res.value.user)
}

class UpdateUser(val userId:Int, val user:User)
  extends AbstractFormPostRequest[UserListValue,UserListValue]("user.update", SelfValueFactory(), user.formParams(userId):_*)
  with ValueFactory[UserListValue,UserListValue] {

  def createValue[A <: UserListValue, B >: UserListValue](request: Request[A, B], in: UserListValue, rawValue: BasicHttpValue,executionContext:ExecutionContext) = {
    new UserListValue(user,rawValue,executionContext) {
      override lazy val id:Option[Int] = Some(userId)
    }
  }
}
