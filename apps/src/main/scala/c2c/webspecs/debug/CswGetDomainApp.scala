package c2c.webspecs
package debug

import geonetwork._
import c2c.webspecs.geonetwork.csw._
import DomainParameters._
import c2c.webspecs.login.LoginRequest
object CswGetDomainApp extends App {
  ExecutionContext.withDefault{ implicit context =>

    val res = (LoginRequest("admin","admin") then CswGetDomain(GetRecordsResultType,DescribeRecordOutputFormat))(None)
    println(res.value.xml.right.get)
  }
}