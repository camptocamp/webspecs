package c2c.webspecs
package debug

import geonetwork._
import c2c.webspecs.geonetwork.csw._
import DomainParameters._
import c2c.webspecs.login.LoginRequest
object CswGetRecordsApp extends WebspecsApp {
  println(Log.LoggingConfig.enabled)
  implicit val resolver = new BasicServerResolver("http", "geonetwork/srv/eng"){
//      override def baseServer = "ec2-46-51-142-140.eu-west-1.compute.amazonaws.com"
//              override def baseServer = "www.geocat.ch"
    }
  	LoginRequest("admin","Hup9ieBe").execute()
//  	val filter = PropertyIsEqualTo("hasLinkageURL", "y")
  	val filter = PropertyIsEqualTo("_indexingError", "1")  
  	//val filter = PropertyIsEqualTo("anyText", "*")
//    val res = CswGetRecordsRequest(filter.xml, outputSchema=OutputSchemas.CheIsoRecord, resultType=ResultTypes.hits, maxRecords = 1, url="http://www.geocat.ch/geonetwork/srv/deu/csw")()

  	//val nf = PropertyIsLike("AnyText","*pmt")
    val res = CswGetRecordsRequest(
        filter.xml,
        outputSchema=OutputSchemas.Record,
        maxRecords = 20000,
        resultType=ResultTypes.results).execute()
    println(res.value.getXml)
}