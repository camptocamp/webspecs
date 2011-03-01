package c2c.webspecs
package geonetwork


import org.specs.Specification
import UserProfiles._


abstract class GeonetworkSpecification(userProfile:UserProfile = Editor) extends Specification {
  implicit val config = GeonetConfig(userProfile,getClass().getSimpleName)
  implicit val context = new DefaultExecutionContext()

  lazy val UserLogin = config.login


  ExecutionContext.withDefault { implicit context =>
    doBeforeSpec (config.setUpTestEnv)

    doAfterSpec {
      context.close()
      config.tearDownTestEnv
    }
  }
}
