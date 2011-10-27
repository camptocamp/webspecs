package c2c.webspecs
package debug

import geonetwork._
import c2c.webspecs.geonetwork.csw._
import DomainParameters._
import c2c.webspecs.login.LoginRequest
object CswGetRecordByIdApp extends App {
  ExecutionContext.withDefault{ implicit context =>
  	//LoginRequest("admin","admin")()
//  	val filter = PropertyIsEqualTo("hasLinkageURL", "y")
    
    val res = CswGetRecordById("67047f47-13d7-491a-bb7e-bb2c8016582d", outputSchema=OutputSchemas.GmdNamespace, resultType=ResultTypes.resultsWithSummary)()
    println(res.value.getXml)
  }
}