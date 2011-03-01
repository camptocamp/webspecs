package c2c.webspecs
package geonetwork

import java.util.UUID
import Properties.TEST_TAG
import xml.Node

object UserProfiles {
  sealed abstract class UserProfile(val alternatives:String*) {
    val name = toString()
    val allNames = name +: alternatives
  }
  case object Guest extends UserProfile
  case object RegisteredUser extends UserProfile
  case object Editor extends UserProfile
  case object UserAdmin extends UserProfile
  case object Reviewer extends UserProfile
  case object Admin extends UserProfile("Administrator")

  val all = Guest :: RegisteredUser :: Editor :: Reviewer :: UserAdmin :: Admin :: Nil

  def withName(name:String) = all find {_.allNames contains name}
}

object User {
  def fromRecord(record:Node) = {
    def get(name:String) = record \ name text
    val id = get("id")
    val username = get("username")
    val password = get("password")
    val surname = get("surname")
    val name = get("name")
    val profileName = get("profile")
    val profile = UserProfiles.withName(profileName) getOrElse {
      Log(Log.Warning, "Unable to find a profile for profile: "+profileName)
      UserProfiles.Guest
    }
    val email = get("email")
    User(Some(id),username,password,surname,name,email,profile = profile)
  }
}

case class User(val idOption:Option[String]=None,
                username:String=(TEST_TAG+UUID.randomUUID.toString.takeRight(5)),
                password:String=TEST_TAG,
                surname:String=TEST_TAG,
                name:String=TEST_TAG,
                email:String=TEST_TAG+"@camptocamp.com",
                position:LocalisedString=LocalisedString(TEST_TAG),
                profile:UserProfiles.UserProfile=UserProfiles.Guest,
                groups:Traversable[String]=Nil) {
  require(profile == UserProfiles.Guest && groups.nonEmpty || profile != UserProfiles,
     "Shared users are not part of groups: profile="+profile+", groups="+(groups mkString ","))

  def id(implicit executionContext:ExecutionContext) = idOption orElse {
    ListUsers(None).value find {_.username == username}
  }

  def formParams():List[(String,String)] = ("username" -> username ::
    "password" -> password ::
    "password2" -> password ::
    "name" -> name ::
    "surname" -> surname ::
    "profile" -> profile.toString ::
    position.formParams("position")) :::
    (if (groups.isEmpty) Nil else List("groups" -> (groups mkString ",")))
}

case class UserListValue(user:User, basicValue:BasicHttpValue,executionContext:ExecutionContext) extends XmlValue {
  lazy val id:Option[String] = withXml{
    xml =>
      val tables = xml \\ "table"
      val correctTable = tables filter {_ \ "tr" \ "th" nonEmpty}
      val row = correctTable \ "tr" find { n =>
        (n \ "td" headOption).exists {_.text.trim == user.username}
      }
      row.get \\ "@onclick" find {_.text contains "deleteUser"} flatMap {onclick => XmlUtils.extractId(onclick.text)}
  }

  lazy val updatedUser:Option[User] = id match {
    case Some(userId) =>
      val response = GetUser(userId)(None)(executionContext)
      Some(response.value.user)
    case None => None
  }
}

case class GetUserValue(id:String, basicValue:BasicHttpValue) extends XmlValue with IdValue {
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
    User(Some(id),"","",surname,name,email,position)
  }
}

case object ListUsers extends AbstractGetRequest[Any,List[User]]("xml.user.list", SelfValueFactory()) with BasicValueFactory[List[User]] {
  def createValue(rawValue: BasicHttpValue) = {
    rawValue.toXmlValue.withXml(xml => {
      val users = xml \\ "record" map {record =>
        User.fromRecord(record)
      }

      users.toList
    })
  }
}

object GetUser {
  def fromUserName(userName:String):Request[Any,GetUserValue] = {
    ListUsers then {response =>
      val user = response.value.find{_.username == userName} getOrElse {throw new IllegalArgumentException("Unable to find user with username: "+userName)}
      val id = user.idOption getOrElse(throw new Error("No ID provided for user "+userName))
      GetUser(id)
    }
  }
}
case class GetUser(userId:String)
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
  def apply(id:String, user:User) = new UpdateUser(id,user)
  def apply(user:User) = (res:Response[UserListValue]) => new UpdateUser(res.value.id.get,res.value.user)
}

class UpdateUser(val userId:String, val user:User)
  extends AbstractFormPostRequest[UserListValue,UserListValue]("user.update", SelfValueFactory(), user.formParams():_*)
  with ValueFactory[UserListValue,UserListValue] {

  def createValue[A <: UserListValue, B >: UserListValue](request: Request[A, B], in: UserListValue, rawValue: BasicHttpValue,executionContext:ExecutionContext) = {
    new UserListValue(user.copy(idOption = Some(userId)),rawValue,executionContext) {
      override lazy val id:Option[String] = Some(userId)
    }
  }
}
