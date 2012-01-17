package c2c.webspecs
package debug

import geonetwork._
import c2c.webspecs.geonetwork.csw._
import DomainParameters._
import c2c.webspecs.login.LoginRequest
object CswGetRecordByIdApp extends WebspecsApp {
  	//LoginRequest("admin","admin")()
//  	val filter = PropertyIsEqualTo("hasLinkageURL", "y")
    
    val res = CswGetRecordById("bd57c354-d13d-4fcb-8d87-e55997e93b8f").execute()
    println(res.value.getXml)
}