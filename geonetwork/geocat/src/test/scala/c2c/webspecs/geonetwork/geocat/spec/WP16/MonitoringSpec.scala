package c2c.webspecs
package geonetwork
package geocat
package spec.WP16

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step

import c2c.webspecs.geonetwork.UserProfiles


/**
 * We have migrated to metrics so we need to update this to check metrics instead
 */
@RunWith(classOf[JUnitRunner]) 
class MonitoringSpec extends GeocatSpecification(UserProfiles.Admin) {  def is =
	"Xsl custom metadata XML output".title 														^ Step(setup)           ^
	        "Add some metadata so that healthchecks will work"                                  ^ Step(importMd)        ^
			"Get the results of the ${metrics} webservice"										! checkMonitorReport    ^
			"Get the results of the ${healthcheck} webservice"									! checkMonitorReport    ^
			"Get the results of the ${threads} webservice" 									! checkMonitorReport    ^
			"Get the results of the ${ping} webservice"  										! checkMonitorReport    ^
			"Checking ${criticalhealthcheck} status"										    ! checkServicesStatus   ^
			"Checking ${warninghealthcheck} status"											    ! checkServicesStatus   ^
			"Checking ${expensivehealthcheck} status"										    ! checkServicesStatus		      ^
																		 						end ^ Step(tearDown)
			
  def importMd = super.importMd(3,"/geocat/data/comprehensive-iso19139che.xml",datestamp)
  def check(desc: String, urlRoot: String) = {
    val check = extract1(desc)
    val request = GetRequest("http://"+Properties.testServer+"/"+urlRoot+check, 'ignorewhitelist -> true)
    config.adminLogin.execute()
    val adminLoggedInResult = request.execute()
    UserLogin.execute()
    val editorLoggedInResult = request.execute()
    (adminLoggedInResult must haveA200ResponseCode) and
        (editorLoggedInResult must haveAResponseCode(401))
  }

  val checkMonitorReport = (desc: String) => check(desc, "geonetwork/monitor/")

  def checkServicesStatus = (desc:String) => check(desc, "geonetwork/")

}
