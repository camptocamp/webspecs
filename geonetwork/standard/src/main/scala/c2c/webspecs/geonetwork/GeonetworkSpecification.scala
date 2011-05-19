package c2c.webspecs
package geonetwork


import org.specs2._
import specification._
import UserProfiles._
import c2c.webspecs.ExecutionContext

object GeonetworkTestContext extends BeforeAfterEach {
	 def before = ExecutionContext.withDefault { implicit context =>config.setUpTestEnv }
	 def after = ExecutionContext.withDefault { implicit context =>
      context.close()
      config.tearDownTestEnv
    }
}
abstract class GeonetworkSpecification(userProfile:UserProfile = Editor) extends Specification {
  implicit val config = GeonetConfig(userProfile,getClass().getSimpleName)
  implicit val context = new DefaultExecutionContext()
  implicit val resourceBase = getClass

  lazy val UserLogin = config.login

}
