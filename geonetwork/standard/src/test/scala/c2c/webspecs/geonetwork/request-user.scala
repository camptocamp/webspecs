package c2c.webspecs
package geonetwork

import java.util.UUID
import Properties.TEST_TAG
import xml.Node

object UserProfiles {
   abstract class UserProfile(val alternatives:String*) { 
    val name = toString()
    val allNames = name +: alternatives
  }
  case object Guest extends UserProfile
  case object RegisteredUser extends UserProfile
  case object Editor extends UserProfile
  case object UserAdmin extends UserProfile
  case object Reviewer extends UserProfile
  case object Admin extends UserProfile("Administrator") {
    override val toString = alternatives.head
  }

  var all = Guest :: RegisteredUser :: Editor :: Reviewer :: UserAdmin :: Admin :: Nil

  def withName(name:String) = all find {_.allNames contains name}
}

object User {
  def apply(uuid:UUID,profile:UserProfiles.UserProfile):User = {
    User(None, username=uuid.toString(),email = uuid+"@cc2c.com", profile = profile)
  }
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
    new User(Some(id),username,password,surname,name,email,profile = profile) with UserRef {
      lazy val userId = idOption.get
    }
  }
}
object UserRef {
  def apply(id:String) = new UserRef{val userId=id}
}
trait UserRef {
  def userId:String
}
case class User(val idOption:Option[String]=None,
                username:String=(TEST_TAG+UUID.randomUUID.toString.takeRight(5)),
                password:String=TEST_TAG,
                surname:String=TEST_TAG,
                name:String=TEST_TAG,
                email:String=TEST_TAG+"@camptocamp.com",
                position:LocalisedString=LocalisedString(TEST_TAG),
                profile:UserProfiles.UserProfile=UserProfiles.Guest,
                contactInstructions:Option[LocalisedString] = None,
                groups:Traversable[String]=Nil) {
  require(profile == UserProfiles.Guest && groups.nonEmpty || UserProfiles.all.contains(profile), 
     "Shared users are not part of groups: profile="+profile+", groups="+(groups mkString ","))

  def this(user:User) = this(
    idOption = user.idOption,
    username = user.username,
    password = user.password,
    surname = user.surname,
    name = user.name,
    email = user.email,
    position = user.position,
    profile = user.profile,
    contactInstructions = user.contactInstructions,
    groups = user.groups)
  def loadId(implicit executionContext:ExecutionContext, uriResolver:UriResolver):Option[String] = idOption orElse {
    ListUsers.execute().value find {_.username == username} map {_.userId}
  }

  def formParams():List[Param[Any,String]] = {
    val idParam = idOption.map(id => SP("id" -> id)).toList
    val ciParam = contactInstructions.toList.flatMap{_.formParams("contactinstructions")}
    val params = (P("username", username) ::
	    SP("password", password) ::
	    SP("password2", password) ::
	    SP("name", name) ::
	    SP("surname", surname) ::
	    SP("email", email) ::
        SP("profile", profile.toString) :: 
	    position.formParams("positionname")) 
    
    val groupParam = (if (groups.isEmpty) Nil else List(SP("groups" -> (groups mkString ","))))
    idParam ::: params ::: ciParam ::: groupParam
  }
}

case class UserListValue(user:User, basicValue:BasicHttpValue,executionContext:ExecutionContext, uriResolver:UriResolver) extends XmlValue {
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
      val response = GetUser(userId).execute(None)(executionContext,uriResolver)
      Some(response.value.user)
    case None => None
  }
}

class UserValue(val user:User, val basicValue:BasicHttpValue) extends XmlValue with UserRef {
  def userId = user.idOption.get
  def loadUser(implicit context:ExecutionContext, uriResolver:UriResolver) = 
    GetUser(userId).execute(None).value.user
}

class GetUserValue(override val userId:String, basicValue:BasicHttpValue) extends UserValue(null, basicValue) {
  override val user = withXml { xml =>
    // TODO need another request to get username and profile
    val record = (xml \ "response" \ "record")
    val username = (record \\ "username").text.trim
    val password = (record \\ "password").text.trim
    val surname = (record \\ "surname").text.trim
    val name = (record \\ "name").text.trim
    val email = (record \\ "email").text.trim
    def localizedLookup(tagName:String)= {
      val tag = record \\ tagName
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
    User(Some(userId),username,password,surname,name,email,position)
  }
}

case object ListUsers extends AbstractGetRequest[Any,List[User with UserRef]]("xml.user.list", SelfValueFactory()) with BasicValueFactory[List[User with UserRef]] {
  def createValue(rawValue: BasicHttpValue) = {
    rawValue.toXmlValue.withXml(xml => {
      val users = xml \\ "response" \\ "record" map {record =>
        User.fromRecord(record)
      }

      users.toList
    })
  }
}

object GetUser {
  def fromUserName(userName:String):Request[Any,UserValue] = {
    ListUsers then {(response:Response[List[User with UserRef]]) =>
      val user = response.value.find{_.username == userName} getOrElse {throw new IllegalArgumentException("Unable to find user with username: "+userName)}
      val id = user.userId
      GetUser(id)
    }
  }
}
case class GetUser(userId:String)
  extends AbstractGetRequest[Any,UserValue](
    "user.get!",
    SelfValueFactory(),
    P("id", userId.toString)) with BasicValueFactory[GetUserValue] {
  override def createValue(rawValue: BasicHttpValue) = new GetUserValue(userId,rawValue)
}

case class CreateUser(user:User)
  extends AbstractFormPostRequest[Any,UserValue](
    "user.update",
    SelfValueFactory(),
    (P("operation", "newuser") :: user.formParams):_*)
  with ValueFactory[Any,UserValue] {

  override def createValue[A <: Any, B >: UserValue](request: Request[A, B], in: Any, rawValue: BasicHttpValue,executionContext:ExecutionContext, uriResolver:UriResolver) = {
    new UserValue(user,rawValue) {
      override lazy val userId = user.loadId(executionContext, uriResolver).get
    }
  }
}
object DeleteUser {
	def apply() = (user:UserRef) => new DeleteUser(user.userId)
	def apply(user:UserRef) = new DeleteUser(user.userId)
}
case class DeleteUser(userId:String) extends AbstractGetRequest[Any,IdValue]("user.remove", ExplicitIdValueFactory(userId), SP("id" -> userId))

case class UpdateUser(val user:User)
  extends AbstractFormPostRequest[Any,UserValue]("user.update", SelfValueFactory(), P("operation", "fullupdate") :: user.formParams() : _*)
  with ValueFactory[Any,UserValue] {
  assert(user.profile.toString() != "Shared")

  def createValue[A <: Any, B >: UserValue](request: Request[A, B], in: Any, rawValue: BasicHttpValue,executionContext:ExecutionContext, uriResolver:UriResolver) = {
    new UserValue(user.copy(idOption = Some(user.idOption.get)),rawValue)
  }
}
