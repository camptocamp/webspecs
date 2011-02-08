package org.fao.geonet

import System.{getProperty => sysprop, getenv => envprop}
import collection.JavaConverters._
import java.net.{InetAddress, HttpURLConnection, URL}
import java.util.{UUID, Properties}
import collection.mutable.HashMap

class ExceptionChain(first:Throwable, exception:Throwable*) extends Exception(first.getMessage,first) {
  def all = first +: exception
  override def getMessage = "Multiple Exceptions:"+all.map{_.getMessage}.mkString("\n\t","\n\t","\n--------------------------------\n")

  override def getStackTrace = first.getStackTrace

}
object Config extends Log {
  val TEST_TAG = "{automated_test_metadata}"

  import UserProfiles.UserProfile
  val instances = new HashMap[(UserProfile,String), Config]()
  def apply(userProfile:UserProfile, name:String):Config = synchronized {
    instances.get(userProfile -> name) getOrElse {
      val config = new Config(SystemLifeCycle(),userProfile, name)
      instances.put(userProfile -> name,config)
      config
    }
  }

  final val TEST_URL_KEY = "test.server"
  final val LOCALE_KEY = "locale"
  final val ADMIN_USER_KEY = "admin.user"
  final val ADMIN_USER_PASS = "admin.pass"

  def serviceUrl(service: String, params: (String, String)*) = {
    val baseURL = property(TEST_URL_KEY) getOrElse "localhost:8080/geonetwork/srv/"
    val locale = property(LOCALE_KEY) getOrElse "eng"
    val serviceUrl = "http:/" :: baseURL :: "geonetwork/srv" :: locale :: service :: Nil mkString "/"

    if (params.isEmpty) {
      serviceUrl
    } else {
      val paramString = params.map{
        case (key, value) => key + "=" + value
      } mkString ("?", "&", "")
      serviceUrl + paramString
    }
  }
  def connect(service: String, params: Map[String, String]) = {
    val urlString = serviceUrl(service, params.toSeq:_*)
    val url = new URL(urlString)
    log(Connection, "Opening connection to " + url)
    url.openConnection.asInstanceOf[HttpURLConnection]
  }

  def properties = Option(getClass.getClassLoader.getResourceAsStream("test.conf")) map {
    in =>
      val p = new Properties()
      p.load(in)
      p.asScala.toMap.asInstanceOf[Map[String, String]]
  }

  def property(key: String) = Option(sysprop(key)) orElse Option(envprop(key)) orElse properties.flatMap{
    _.get(key)
  }

  def getProperty(key:String) = property(key) getOrElse{
    throw new IllegalStateException(key+" is required to be defined.  Most likely it is expected to be a jvm option")
  }
  lazy val adminLogin = Login(getProperty(ADMIN_USER_KEY), getProperty(ADMIN_USER_PASS))
  lazy val userPrefix = "atest_" + InetAddress.getLocalHost.getHostName+"_"
  def extractId(li: String): Option[String] = {
    """.*id=(\d+).*?""".r.findFirstMatchIn(li) map {
      _.group(1)
    }
  }

  lazy val usersList = adminLogin then Get("user.list")

  def findUserIds(r: Response=usersList.assertPassed)(matcher: String => Boolean): Traversable[String] = {
    MdRequestUtil.withXml(r) { xml =>
       val tables = xml \\ "table"
      val correctTable = tables filter {_ \ "tr" \ "th" nonEmpty}
      val row = correctTable \ "tr" find { n =>
        (n \ "td" headOption).exists {td => matcher(td.text.trim)}
      }
      row.toList flatMap {_ \\ "@onclick" filter {_.text contains "deleteUser"} flatMap {onclick => Config.extractId(onclick.text)}}
    }
  }
  def findUsers(matcher:String => Boolean)(requestBuilder:Traversable[String] => Request) = usersList then {
    (r: Response) =>
      val ids = findUserIds(r)(matcher)
      requestBuilder(ids)
  }

  lazy val groupsList = adminLogin then Get("group.list")
  def findGroupIds(r:Response = groupsList.assertPassed)(matcher:String => Boolean) = {
    val deleteButton = r.text.right.get.lines.dropWhile(l => !matcher(l)).dropWhile(l => !l.contains("delete1"))
    (deleteButton flatMap {line => extractId(line).iterator} toTraversable)
  }
  def findGroups(matcher:String => Boolean)(creator: Traversable[String] => Request) = groupsList then {
    (r: Response) =>
      val ids = findGroupIds(r)(matcher)
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

  def inputStream(path:String) = Option(getClass.getClassLoader.getResourceAsStream(path)) getOrElse {throw new IllegalArgumentException(path+" is not an available resource")}
}

class Config(lifeCycle:SystemLifeCycle, userProfile:UserProfiles.UserProfile, val specName:String) extends Log {
  import Config._

  val constants = new Constants(this,userProfile)

  def setUpTestEnv() = {
    try {
      log(LifeCycle, "Setup Test Environment")

      lifeCycle.setup(this)

      log(LifeCycle, "Done Setting up Test Environment \r\n\r\n\r\n")
    } catch {
      case e:Throwable =>
        util.control.Exception.catching(classOf[Throwable]).either(tearDownTestEnv) match {
          case Right(_) => throw e
          case Left(error:Throwable) => throw new ExceptionChain(e,error)
        }
    }
  }


  def tearDownTestEnv() = {

    try {
      log(LifeCycle, "\r\n\r\n\r\nTearing down Test Environment")
      lifeCycle.tearDown(this)
    } catch {
      case e:Throwable =>
        System.err.println("Error occurred during teardown: "+e)
        e.printStackTrace(System.err)
    }
  }

  lazy val login = {
    Login(constants.user,constants.pass){
      response =>
        assert(response.responseCode == 200, "Failed to login as user, code: "+response.responseCode)
        response
    }
  }
}

