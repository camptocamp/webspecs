package c2c.webspecs
package geonetwork


import collection.mutable.HashMap
import java.util.UUID
import xml.Node
import c2c.webspecs.login.LoginRequest
import csw._

object GeonetConfig extends Log {

  val LOCALE_KEY = "locale"

  import UserProfiles.UserProfile
  val instances = new HashMap[(UserProfile,String), GeonetConfig]()
  def apply(userProfile:UserProfile, name:String):GeonetConfig = synchronized {
    instances.get(userProfile -> name) getOrElse {
      val config = new GeonetConfig(userProfile, name)
      instances.put(userProfile -> name,config)
      config
    }
  }
}

class GeonetConfig(val userProfile:UserProfiles.UserProfile, specName:String)
  extends Config(specName) {

  def ADMIN_USER_KEY = "admin.user"
  def ADMIN_USER_PASS = "admin.pass"

  def login = LoginRequest(user,pass)
  private implicit val resolver=Config.defaultUriResolver

  def adminLogin = LoginRequest(Properties.get(ADMIN_USER_KEY), Properties.get(ADMIN_USER_PASS))
  lazy val userPrefix = "atest_" + UUID.randomUUID.toString.takeRight(8) +"_"

  def extractId(li: String): Option[String] = XmlUtils.extractId(li)

  lazy val usersList: List[User with UserRef] = ExecutionContext.withDefault{implicit c =>
    val l = (adminLogin then ListUsers).execute().value
    l
  }
  lazy val groupsList:List[GroupValue] = ExecutionContext.withDefault{implicit c => 
    (adminLogin then ListGroups).execute().value}

def mdSearchXml(props:Traversable[PropertyIsLike]) =
    <csw:GetRecords xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" service="CSW" version="2.0.2" resultType="results" startPosition="1" maxRecords="100">
      <csw:Query typeNames="csw:Record">
        <csw:ElementSetName>full</csw:ElementSetName>
        <csw:Constraint version="1.0.0">
          <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
            <ogc:Or>
              { props map {_.xml} }
            </ogc:Or>
          </ogc:Filter>
        </csw:Constraint>
      </csw:Query>
    </csw:GetRecords>

  lazy val user = Properties("user") getOrElse userPrefix+specName
  lazy val pass = Properties("pass") getOrElse UUID.randomUUID.toString.takeRight(8)

  lazy val groupId:String = group.id

  lazy val group = findGroupFromProperty getOrElse findUsersGroup

  private lazy val findGroupFromProperty = Properties("group").flatMap {name =>
    groupsList.find(_.name == name)
  }

  private lazy val findUsersGroup:GroupValue = ExecutionContext.withDefault{ implicit c =>
    val groups = (GetUserGroups.setIn(userId).execute()).value
    val group = groups.find{_.name == user} orElse {groups.find{_.id.toInt > 1}}
    group getOrElse {
      throw new IllegalStateException("A group was not found that matches the userYou must call setUpTestEnv before calling groupId")
    }
  }

  lazy val userId = usersList.find(_.username == user).map{user =>
    user.userId
  } getOrElse {
    throw new IllegalStateException("You must call setUpTestEnv before calling userId")
  }

  private def sampleTemplates(filter:Node):List[String] = ExecutionContext.withDefault{ implicit context =>
    val xml = CswGetRecordsRequest(filter, maxRecords = 20, resultType = ResultTypes.results).execute().value.xml

    Log(Log.Constants, xml)
    val ids = xml.fold(throw _, _ \\ "info" \ "id")
    assert(ids.nonEmpty, "No template metadata found" )
    ids.toList map {_.text}
  }
  lazy val sampleDataTemplateIds = {
    val isTemplate = PropertyIsEqualTo("_isTemplate","y").xml
    val data = PropertyIsEqualTo("type","dataset").xml
    sampleTemplates(<ogc:And>{isTemplate}{data}</ogc:And>)
  }
  lazy val sampleServiceTemplateIds = {
    val isTemplate = PropertyIsEqualTo("_isTemplate","y").xml
    val service = PropertyIsEqualTo("type","service").xml
    sampleTemplates(<ogc:And>{isTemplate}{service}</ogc:And>)
  }
}

