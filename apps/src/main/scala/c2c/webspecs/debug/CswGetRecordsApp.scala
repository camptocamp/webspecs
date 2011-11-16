package c2c.webspecs
package debug

import geonetwork._
import c2c.webspecs.geonetwork.csw._
import DomainParameters._
import c2c.webspecs.login.LoginRequest
object CswGetRecordsApp extends WebspecsApp {
  	LoginRequest("admin","Hup9ieBe").execute()
//  	val filter = PropertyIsEqualTo("hasLinkageURL", "y")
  	val filter = PropertyIsEqualTo("abstract", "1321439222610")// and PropertyIsLike("anyText", "wasser") 
//    val res = CswGetRecordsRequest(filter.xml, outputSchema=OutputSchemas.CheIsoRecord, resultType=ResultTypes.hits, maxRecords = 1, url="http://www.geocat.ch/geonetwork/srv/deu/csw")()

    val res = CswGetRecordsRequest(
        filter.xml, 
        outputSchema=OutputSchemas.Record, 
        resultType=ResultTypes.results, 
        sortBy = List(SortBy("_defaultTitle", true))).execute()(executionContext,new BasicServerResolver("http", "geonetwork/srv/fra"){})
    println(res.value.getXml \\ "title" map (_.text))
}