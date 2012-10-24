package c2c.webspecs
package debug

import c2c.webspecs.geonetwork.csw._
import DomainParameters._
import c2c.webspecs.login.LoginRequest
import c2c.webspecs.geonetwork.GeonetworkSpecification
object CswGetDomainApp extends WebspecsApp {
    val res = (LoginRequest("admin","admin") then CswGetDomain(GetRecordsResultType,DescribeRecordOutputFormat)).execute()
    println(res.value.xml.right.get)
}