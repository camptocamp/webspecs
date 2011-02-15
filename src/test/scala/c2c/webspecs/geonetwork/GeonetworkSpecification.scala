package c2c.webspecs
package geonetwork

import org.specs.Specification
import xml.NodeSeq
import UserProfiles._

abstract class GeonetworkSpecification(userProfile:UserProfile = Editor) extends Specification {
  implicit val config = GeonetConfig(userProfile,getClass().getSimpleName)
  implicit val context = new DefaultExecutionContext()

  lazy val UserLogin = config.login



  doBeforeSpec (config.setUpTestEnv())

  doAfterSpec (config.tearDownTestEnv())
}
