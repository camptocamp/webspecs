package c2c.webspecs
package geonetwork


import org.specs._
import UserProfiles._

abstract class GeonetworkSpecification(userProfile:UserProfile = Editor) extends Specification {
  implicit val config = GeonetConfig(userProfile,getClass().getSimpleName)
  implicit val context = new DefaultExecutionContext()

  lazy val UserLogin = config.login



  doBeforeSpec (
    ExecutionContext.withDefault { implicit context =>config.setUpTestEnv }
  )

  doAfterSpec {
    ExecutionContext.withDefault { implicit context =>
      context.close()
      config.tearDownTestEnv
    }
  }
}
