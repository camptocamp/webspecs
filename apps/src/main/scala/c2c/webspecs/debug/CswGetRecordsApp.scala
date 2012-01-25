package c2c.webspecs
package debug

import geonetwork._
import c2c.webspecs.geonetwork.csw._
import DomainParameters._
import c2c.webspecs.login.LoginRequest
object CswGetRecordsApp extends WebspecsApp {
  println(Log.LoggingConfig.enabled)
  	//LoginRequest("admin","Hup9ieBe").execute()
//  	val filter = PropertyIsEqualTo("hasLinkageURL", "y")
  	//val filter = PropertyIsEqualTo("abstract", "1321439222610")// and PropertyIsLike("anyText", "wasser")
//    val res = CswGetRecordsRequest(filter.xml, outputSchema=OutputSchemas.CheIsoRecord, resultType=ResultTypes.hits, maxRecords = 1, url="http://www.geocat.ch/geonetwork/srv/deu/csw")()

  	//val nf = PropertyIsLike("AnyText","*pmt")
    val res = CswGetRecordsRequest(
        Nil,
        outputSchema=OutputSchemas.Record,
        maxRecords = 10,
        resultType=ResultTypes.results).execute()(executionContext,new BasicServerResolver("http", "geosource/srv/en"){
      override def baseServer = "86.65.192.22"
    })
    println(res.value.getXml)
}