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

  override def setup = {
    super.setup
    config.login(None)
  }
}
