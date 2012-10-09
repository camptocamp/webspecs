package c2c.webspecs
package geonetwork
package geocat
package spec.WP16

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import c2c.webspecs.GetRequest


@RunWith(classOf[JUnitRunner]) 
class TestMetadataExpiredServicesSpec extends GeocatSpecification {  def is =
	"Testing metadata.expired.* services".title 															 ^ Step(setup)                           ^
			"Login as admin"																		 ^ Step(config.adminLogin.execute())             ^
			"Service metadata.expired.form should be accessible as administrator"   				 ! testMetadataExpiredFormService        ^
			"Service metadata.expired.unpublish should not fail with default parameters"   			 ! testMetadataExpiredUnpublishService   ^
			"Service metadata.expired.email should not fail with default parameters"   				 ! testMetadataExpiredMailService        ^
																									   end ^ Step(tearDown)	
			

    def testMetadataExpiredFormService = {
 		GetRequest("metadata.expired.form", "testing" -> true).execute() must haveA200ResponseCode
    }
    def testMetadataExpiredUnpublishService = {
 		GetRequest("metadata.expired.unpublish", "limit" ->  "5", "testing" -> true).execute() must haveA200ResponseCode
    }
    def testMetadataExpiredMailService = {
 		GetRequest("metadata.expired.email", "limit" ->  "3", "testing" -> true).execute() must haveA200ResponseCode
    }		
}
