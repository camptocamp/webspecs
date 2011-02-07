package org.fao.geonet

import org.specs.Specification
import xml.NodeSeq
import UserProfiles._

abstract class GeonetworkSpecification(userProfile:UserProfile = Editor) extends Specification {
    val config:Config = Config(userProfile,getClass().getSimpleName)
    implicit val constants = config.constants
    lazy val UserLogin = config.login
    def withXml[R](response:Response)(f:NodeSeq => R):R = MdRequestUtil.withXml(response)(f)

    doBeforeSpec (config.setUpTestEnv())

    doAfterSpec (config.tearDownTestEnv())
}
