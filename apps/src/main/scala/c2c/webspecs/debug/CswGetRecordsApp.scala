package c2c.webspecs
package debug

import c2c.webspecs.geonetwork.csw._
import c2c.webspecs.login.LoginRequest
import java.util.zip.ZipFile
import c2c.webspecs.geonetwork.geocat.spec.WP7.ZipFileValueFactory
import c2c.webspecs.geonetwork.geocat.GeocatSpecification
import c2c.webspecs.geonetwork.GeonetworkSpecification
import c2c.webspecs.geonetwork.geocat.GeocatConstants

object CswGetRecordsApp extends WebspecsApp {

    override def referenceSpecClass = classOf[GeocatSpecification]
    
  val req = CswGetRecordsRequest(PropertyIsEqualTo("Identifier", "bb3fdeee-ae25-45ec-830f-aec31c58ce70").xml,
        maxRecords = 1,
        resultType = ResultTypes.results,
        outputSchema = OutputSchemas.Record,
        url = "ger/csw")
  val response = (LoginRequest("admin", "admin") then req).execute()

  println(response.value.getXml)
  
}