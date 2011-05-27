package c2c.webspecs
package debug

import geonetwork._
import c2c.webspecs.geonetwork.DomainParameters._
object CswGetDomainApp extends App {
  ExecutionContext.withDefault{ implicit context =>

    val res = (Login("admin","admin") then CswGetDomain(GetRecordsResultType,DescribeRecordOutputFormat))(None)
    println(res.value.xml.right.get)
  }
}