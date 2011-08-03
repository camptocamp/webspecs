package c2c.webspecs
package geonetwork
package geocat
package spec.WP16

import scala.xml.NodeSeq
import org.apache.http.entity.mime.content.StringBody
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.ImportStyleSheets.NONE
import c2c.webspecs.geonetwork.csw.OutputSchemas._
import c2c.webspecs.geonetwork._
import c2c.webspecs.GetRequest
import c2c.webspecs.GetRequest
import c2c.webspecs.Response
import c2c.webspecs.XmlValue
import c2c.webspecs.GetRequest
import c2c.webspecs.GetRequest


@RunWith(classOf[JUnitRunner]) 
class TestMetadataExpiredServicesSpec extends GeocatSpecification(UserProfiles.Admin) {  def is =
	"Testing metadata.expired.* services".title 															 ^ Step(setup)                           ^
			"Login as admin"																		 ^ Step(config.adminLogin())             ^
			"Service metadata.expired.form should be accessible as administrator"   				 ! testMetadataExpiredFormService        ^
			"Service metadata.expired.unpublish should not fail with default parameters"   			 ! testMetadataExpiredUnpublishService   ^
			"Service metadata.expired.email should not fail with default parameters"   				 ! testMetadataExpiredMailService        ^
																									   end ^ Step(tearDown)	
			

    def testMetadataExpiredFormService = {
 		GetRequest("metadata.expired.form")(Nil) must haveA200ResponseCode
    }
    def testMetadataExpiredUnpublishService = {
 		GetRequest("metadata.expired.unpublish", "limit" ->  "5")(Nil) must haveA200ResponseCode
    }
    def testMetadataExpiredMailService = {
 		GetRequest("metadata.expired.email", "limit" ->  "3")(Nil) must haveA200ResponseCode
    }		
}
