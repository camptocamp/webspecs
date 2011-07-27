package c2c.webspecs
package geonetwork
package geocat
package spec.WP16

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step

import c2c.webspecs.geonetwork.UserProfiles


@RunWith(classOf[JUnitRunner]) 
class MonitoringSpec extends GeocatSpecification(UserProfiles.Admin) {  def is =
	"Xsl custom metadata XML output".title 														^ Step(setup)                     ^
			"Login as admin"																	^ Step(config.adminLogin()) 	  ^
			"Check if the monitor webservice which outputs its report as HTML is available"		^ Step(testMonitoringReport)    ^
			"Get the results of the monitoring webservice"										^ Step(getMonitoringXmlReport)    ^
			"Checking ${db} status"																! checkServicesStatus		      ^
			"Checking ${cswService_getrecords} status"											! checkServicesStatus		      ^
			"Checking ${freediskService} status"												! checkServicesStatus		      ^
			"Checking ${cswService_capabilities} status"										! checkServicesStatus		      ^
//			"Check ${print_service} status"														! checkServicesStatus		      ^
																		 						end ^ Step(tearDown)
			
			
			
  lazy val getMonitoringXmlReport = {
  	val xmlMonitoringResult = GetRequest("monitoring")(Nil)
	xmlMonitoringResult must haveA200ResponseCode
  	val response = xmlMonitoringResult.value.getXml
    response
  }

  def testMonitoringReport =  {
    val ret = GetRequest("monitoring.report")(Nil)
    ret must haveA200ResponseCode
  }

  def checkServicesStatus = (desc:String) => {
    val strStatus = (getMonitoringXmlReport \\ extract1(desc) \ "status").text
    strStatus must_== "ok"
  }

}
