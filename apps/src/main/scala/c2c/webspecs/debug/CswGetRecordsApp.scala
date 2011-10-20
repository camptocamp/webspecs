package c2c.webspecs
package debug

import geonetwork._
import c2c.webspecs.geonetwork.csw._
import DomainParameters._
import c2c.webspecs.login.LoginRequest
object CswGetRecordsApp extends App {
  ExecutionContext.withDefault{ implicit context =>
  	LoginRequest("admin","admin")
//  	val filter = PropertyIsEqualTo("hasLinkageURL", "y")
  	val filter = PropertyIsEqualTo("protocol", "OGC:WMS-1.1.1-http-get-map")
    val res = CswGetRecordsRequest(filter.xml, resultType=ResultTypes.resultsWithSummary)()
    res.value.xml.right.get \\ "id" foreach (id => println(id.text))
  }
}