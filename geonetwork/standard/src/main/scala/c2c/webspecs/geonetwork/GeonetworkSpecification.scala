package c2c.webspecs
package geonetwork


import org.specs2._
import specification._
import UserProfiles._
import c2c.webspecs.ExecutionContext


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

  object GeonetworkTestContext extends BeforeAfterEach {
    def before = ExecutionContext.withDefault {
      implicit context => config.setUpTestEnv
    }

    def after = ExecutionContext.withDefault[Unit] {
      implicit context =>
        context.close()
        config.tearDownTestEnv
    }
  }

  def is = GeonetworkTestContext(spec)

  def spec : Fragments
}
