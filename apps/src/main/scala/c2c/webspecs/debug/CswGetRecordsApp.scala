package c2c.webspecs
package debug

import geonetwork._
import c2c.webspecs.geonetwork.csw._
import DomainParameters._
import c2c.webspecs.login.LoginRequest
object CswGetRecordsApp extends WebspecsApp {
  	//LoginRequest("admin","Hup9ieBe").execute()
//  	val filter = PropertyIsEqualTo("hasLinkageURL", "y")
  	val filter = PropertyIsLike("keyword", "e-geo.ch geoportal")// and PropertyIsLike("anyText", "wasser") 
//    val res = CswGetRecordsRequest(filter.xml, outputSchema=OutputSchemas.CheIsoRecord, resultType=ResultTypes.hits, maxRecords = 1, url="http://www.geocat.ch/geonetwork/srv/deu/csw")()
    val res = CswCQLGetRecordsRequest(
        filter.cql, 
        outputSchema=OutputSchemas.CheIsoRecord, 
        resultType=ResultTypes.hits, 
        maxRecords = 1, 
        url="http://www.geocat.ch/geonetwork/srv/fra/csw").execute()
    println(res.value.getXml)
}