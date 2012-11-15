package c2c.webspecs
package geonetwork
package geocat.spec.WP6

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import c2c.webspecs.geonetwork.geocat.GeocatSpecification

@RunWith(classOf[JUnitRunner])
class CswGetRecordsSpec extends c2c.webspecs.geonetwork.spec.csw.search.CswGetRecordsSpec with GeocatSpecification {
  
  override def metadataToImport = "/geocat/data/metadata.iso19139.che.xml"
}