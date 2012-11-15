package c2c.webspecs
package geonetwork
package spec.misc

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.UserProfiles
import c2c.webspecs.login.LogoutRequest


/**
 * We have migrated to metrics so we need to update this to check metrics instead
 */
@RunWith(classOf[JUnitRunner]) 
class MonitoringSpec extends GeonetworkSpecification {  def is =
	"Monioring Specification".title 														^ Step(setup) ^ sequential ^
	        "Add some metadata so that healthchecks will work"                                  ^ Step(importMd)        ^
			"Get the results of the ${metrics} webservice"										! checkMonitorReport    ^
			"Get the results of the ${healthcheck} webservice"									! checkMonitorReport    ^
			"Get the results of the ${threads} webservice" 									! checkMonitorReport    ^
			"Get the results of the ${ping} webservice"  										! checkMonitorReport    ^
			"Checking ${criticalhealthcheck} status"										    ! checkServicesStatus   ^
			"Checking ${warninghealthcheck} status"											    ! checkServicesStatus   ^
			"Checking ${expensivehealthcheck} status"										    ! checkServicesStatus		      ^
																		 						end ^ Step(tearDown)
  def mdToImport = "/geonetwork/data/multilingual-metadata.iso19139.xml"
  def importMd = super.importMd(3,mdToImport,datestamp)
  def check(desc: String, urlRoot: String) = {
    val check = extract1(desc)
    val request = GetRequest("http://"+Properties.testServer+"/"+urlRoot+check)
    config.adminLogin.execute()
    val adminLoggedInResult = request.execute()
    LogoutRequest().execute()
    UserLogin.execute()
    val editorLoggedInResult = request.execute()
    LogoutRequest().execute()
    val loggedOutResult = request.execute()
    (adminLoggedInResult must haveA200ResponseCode) and
    	(adminLoggedInResult.basicValue.finalURL.get.getPath() must endWith (check)) and
    	(loggedOutResult.basicValue.finalURL.get.getPath must contain ("login.jsp")) and
        (editorLoggedInResult must haveAResponseCode(403))
  }

  val checkMonitorReport = (desc: String) => check(desc, "geonetwork/monitor/")

  def checkServicesStatus = (desc:String) => check(desc, "geonetwork/")

}
