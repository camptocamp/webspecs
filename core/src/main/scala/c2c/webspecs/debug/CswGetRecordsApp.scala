package c2c.webspecs
package debug

import geonetwork._
import c2c.webspecs.geonetwork.csw._
import DomainParameters._
import c2c.webspecs.login.LoginRequest
object CswGetRecordsApp extends App {
  ExecutionContext.withDefault{ implicit context =>
  	LoginRequest("admin","admin")
  	val filter = PropertyIsEqualTo("hasLinkageURL", "y")
  	println(filter.xml)
    val res = CswGetRecordsRequest(filter.xml, resultType=ResultTypes.hits)()
    println(res.value.xml.right.get)
  }
}