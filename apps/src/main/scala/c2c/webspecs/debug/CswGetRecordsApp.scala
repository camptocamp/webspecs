package c2c.webspecs
package debug

import c2c.webspecs.geonetwork.csw._
import c2c.webspecs.login.LoginRequest
import java.util.zip.ZipFile
import c2c.webspecs.geonetwork.geocat.spec.WP7.ZipFileValueFactory
import c2c.webspecs.geonetwork.geocat.GeocatSpecification
import c2c.webspecs.geonetwork.GeonetworkSpecification

object CswGetRecordsApp extends WebspecsApp {
    def referenceSpecClass = classOf[GeonetworkSpecification]

  val gcResolver = new BasicServerResolver("http", "geonetwork/srv/eng") {
    override def baseServer = "www.geocat.ch"
  }

  val shadowResolver = new BasicServerResolver("http", "geonetwork/srv/eng") {
      override def baseServer = "ec2-176-34-163-138.eu-west-1.compute.amazonaws.com"
  }
  
  val integrationResolver = new BasicServerResolver("http", "geonetwork/srv/eng") {
      override def baseServer = "tc-geocat0i.bgdi.admin.ch"
  }
  
  val oldGeocatResolver = new BasicServerResolver("http", "geonetwork/srv/eng") {
      override def baseServer = "tc-geocat0i.bgdi.admin.ch:9999"
  }
  
  val response = (LoginRequest("admin", "admin") then CswGetRecordsRequest(Nil)).execute()

  println(response.value.getXml)
  
}