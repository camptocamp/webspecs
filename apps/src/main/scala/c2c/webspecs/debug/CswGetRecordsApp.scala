package c2c.webspecs
package debug

import c2c.webspecs.geonetwork.csw._
import c2c.webspecs.login.LoginRequest
import java.util.zip.ZipFile
import c2c.webspecs.geonetwork.geocat.spec.WP7.ZipFileValueFactory
import c2c.webspecs.geonetwork.geocat.GeocatSpecification
import c2c.webspecs.geonetwork.GeonetworkSpecification
import c2c.webspecs.geonetwork.geocat.GeocatConstants
import c2c.webspecs.geonetwork.XmlSearch

object CswGetRecordsApp extends WebspecsApp {

    override def referenceSpecClass = classOf[GeocatSpecification]
    
    
  val req = CswGetRecordsRequest(Nil,
        resultType = ResultTypes.results,
        outputSchema = OutputSchemas.Record)
        while(true) {
  (LoginRequest("admin", "admin") then XmlSearch()).execute().value.summary
        }

  
}