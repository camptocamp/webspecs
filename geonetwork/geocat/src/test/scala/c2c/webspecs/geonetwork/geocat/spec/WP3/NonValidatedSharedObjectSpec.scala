package c2c.webspecs
package geonetwork
package geocat
package spec.WP3



import c2c.webspecs.geonetwork.geocat.GeocatSpecification


import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step


@RunWith(classOf[JUnitRunner])
class NonValidatedSharedObjectSpec extends GeocatSpecification {
	def is = {
	  "Non-validated reusable objects test".title 	                 ^ Step(setup)               ^
	  	   "Login as admin"								             ^ Step(config.adminLogin.execute()) ^
	  	   "Checking the ${reusable.non_validated.admin} webservice" ! ReusableServiceTest       ^
	  	   "Checking the ${reusable.non_validated.list}  webservice" ! ReusableServiceTest       ^
	  	   "Checking the ${xml.reusable.deleted}         webservice" ! ReusableServiceTest       ^
	  	                                                             end ^ Step(tearDown)
	}
	
  def ReusableServiceTest = (descr:String) => {
    val serviceName = extract1(descr)
    val serviceReq  = GetRequest(serviceName).execute()
    serviceReq must haveA200ResponseCode
  }
  // TODO : this is a first shot on testing the service
  
  // This spec should probably test more thoroughly the different services exposed by GeoCat
  // i.e. adding a shared object, invalidating it, listing it, removing it, see if it is
  // listed in the reusable.deleted service output ...
  
  
	
}