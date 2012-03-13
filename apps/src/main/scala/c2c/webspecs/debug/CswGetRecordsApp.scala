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
  	val filter = PropertyIsEqualTo("_isHarvested", "n")
  	//val filter = PropertyIsEqualTo("anyText", "*")
//    val res = CswGetRecordsRequest(filter.xml, outputSchema=OutputSchemas.CheIsoRecord, resultType=ResultTypes.hits, maxRecords = 1, url="http://www.geocat.ch/geonetwork/srv/deu/csw")()

  	//val nf = PropertyIsLike("AnyText","*pmt")
    val res = CswGetRecordsRequest(
        filter.xml,
        outputSchema=OutputSchemas.Record,
        maxRecords = 1000,
        url = "http://www.geocat.ch/geonetwork/srv/eng/csw",
        resultType=ResultTypes.hits).execute()/*(executionContext,new BasicServerResolver("http", "geonetwork/srv/eng"){
      override def baseServer = "ec2-46-51-142-140.eu-west-1.compute.amazonaws.com"
    })*/
    println(res.value.getXml)
}