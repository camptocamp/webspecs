package c2c.webspecs
package geonetwork


import org.specs2._
import specification._
import UserProfiles._
import c2c.webspecs.ExecutionContext
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.MatchResult
import org.specs2.execute.Result
import scala.xml.NodeSeq
import java.util.UUID

@RunWith(classOf[JUnitRunner]) 
abstract class GeonetworkSpecification(userProfile: UserProfile = Editor) extends WebSpecsSpecification[GeonetConfig] {
  implicit val config = GeonetConfig(userProfile, getClass().getSimpleName)
  lazy val UserLogin = config.login

  object template extends Given[String] {
    def extract(text: String): String = extract1(text)
  }
  object randomTemplate extends Given[String] {
    def extract(text: String): String = config.sampleDataTemplateIds(0)
  }

  lazy val uuid = UUID.randomUUID
  
  override def setup = {
    super.setup
    config.login(None)
  }
  
  def hrefInElement(nodeName:String) = 
    (result:Response[NodeSeq]) => {
      val href = (result.value \\ nodeName \@ "xlink:href")
      (href must not beEmpty)
    }
  def hrefHost(nodeName:String) = (result:Response[NodeSeq],s:String) => {
    val href = (result.value \\ nodeName \@ "xlink:href")(0)
    val Matcher = """(http://)?([^:]+):(\d+).*""".r
    val Matcher(_,host,port) = href
    val Matcher(_,expectedHost, expectedPort) = Properties.testServer
    extract1(s).toLowerCase match {
      case "port" => port must be_==(expectedPort)
      case "host" => host must be_==(expectedHost)
    }
  }

}
