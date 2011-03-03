package c2c.webspecs
package geonetwork


import collection.mutable.HashMap
import java.net.InetAddress
import java.util.UUID
import xml.Node

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

  def login = Login(user,pass)

  def adminLogin = Login(Properties.get(ADMIN_USER_KEY), Properties.get(ADMIN_USER_PASS))
  lazy val userPrefix = "atest_" + InetAddress.getLocalHost.getHostName+"_"

  def extractId(li: String): Option[String] = XmlUtils.extractId(li)

  lazy val usersList = adminLogin then GetRequest("user.list")

  def findUserIds(req: Request[Any,XmlValue]=usersList)(matcher: String => Boolean): Traversable[String] =
    ExecutionContext.withDefault { context =>
      req(None)(context).value.withXml{ xml =>
        val tables = xml \\ "table"
        val correctTable = tables filter {_ \ "tr" \ "th" nonEmpty}
        val row = correctTable \ "tr" find { n =>
          (n \ "td" headOption).exists {td => matcher(td.text.trim)}
        }
        row.toList flatMap {_ \\ "@onclick" filter {_.text contains "deleteUser"} flatMap {onclick => extractId(onclick.text)}}
      }
    }
  def  findUsers[Out](matcher:String => Boolean)(requestBuilder:Traversable[String] => Request[Any,Out]) =  {
    val ids = findUserIds(usersList)(matcher)
    requestBuilder(ids)
  }

  lazy val groupsList = adminLogin then GetRequest("group.list")
  def findGroupIds(req:Request[Any,XmlValue] = groupsList)(matcher:String => Boolean) =
    ExecutionContext.withDefault {context =>
      implicit val c = context
      val value = req(None).value
      val deleteButton = value.text.right.get.lines.dropWhile(l => !matcher(l)).dropWhile(l => !l.contains("delete1"))
      (deleteButton flatMap {line => extractId(line).iterator} toTraversable)
    }
  def findGroups[Out](matcher:String => Boolean)(creator: Traversable[String] => Request[Any,Out]) = {
    val ids = findGroupIds(groupsList)(matcher)
    creator(ids)
  }

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
  lazy val groupId = findGroupIds()(_ contains user) match {
    case ids if ids.isEmpty => throw new IllegalStateException("You must call setUpTestEnv before calling groupId")
    case ids => ids.head
  }
  lazy val userId = findUserIds()(_ contains user) match {
    case ids if ids.isEmpty => throw new IllegalStateException("You must call setUpTestEnv before calling userId")
    case ids => ids.head
  }
  private def sampleTemplates(filter:Node):List[String] = ExecutionContext.withDefault{ implicit context =>
    val xml = CswGetRecordsRequest(filter, maxRecords = 20, resultType = ResultTypes.results)(None).value.xml

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

