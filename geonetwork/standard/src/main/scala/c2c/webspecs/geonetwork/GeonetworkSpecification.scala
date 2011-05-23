package c2c.webspecs
package geonetwork


import org.specs2._
import specification._
import UserProfiles._
import c2c.webspecs.ExecutionContext
import accumulating.AccumulatedResponse3
import java.net.{HttpURLConnection, URL}

abstract class GeonetworkSpecification(userProfile: UserProfile = Editor) extends Specification {
  implicit val config = GeonetConfig(userProfile, getClass().getSimpleName)
  implicit val context = new DefaultExecutionContext()
  implicit val resourceBase = getClass

  object template extends Given[String] {
    def extract(text: String): String = extract1(text)
  }
  object randomTemplate extends Given[String] {
    def extract(text: String): String = config.sampleDataTemplateIds(0)
  }

  lazy val UserLogin = config.login


  def setup = ExecutionContext.withDefault {
    implicit context => config.setUpTestEnv
  }
  def tearDown = ExecutionContext.withDefault[Unit] {
    implicit context =>
      context.close()
      config.tearDownTestEnv
  }


  object SampleTemplate extends Given[String]{
    def extract(text: String): String = extract1(text) match {
      case "service" => config.sampleServiceTemplateIds(0)
      case "data" => config.sampleDataTemplateIds(0)
    }
  }
}
