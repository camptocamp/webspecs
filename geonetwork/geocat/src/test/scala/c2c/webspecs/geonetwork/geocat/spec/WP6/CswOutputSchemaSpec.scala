package c2c.webspecs
package geonetwork
package geocat
package spec.WP6

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import scala.xml.NodeSeq
import OutputSchemas._
import c2c.webspecs.geonetwork.csw.CswGetCapabilities
import c2c.webspecs.GetRequest
import org.specs2.execute.Result

@RunWith(classOf[JUnitRunner])
class CswOutputSchemaSpec extends geonetwork.spec.csw.CswOutputSchemaSpec {

  override def testCapabilitliesHasCustomOutputSchema = 
     "must have ${http://www.isotc211.org/2008/gm03_2} as an outputSchema" ! outputSchema ^
      "must have ${GM03_2Record} as an outputSchema" ! outputSchema ^
       "must have ${own} as an outputSchema" ! outputSchema

  override def testCustomOutputSchemas =
    "Getting the metadata previously inserted in iso19139.che output" ! testiso19139che ^
      "Getting the metadata previously inserted in its own format" ! testOwn ^
      "Getting the metadata previously inserted in http://www.isotc211.org/2008/gm03_2 output" ! testGM03Url ^
      "Getting the metadata previously inserted in GM03_2Record output" ! testGM03_2

  def testiso19139che = {
    val getRecordResult = CswGetRecordById(importMetadataId, geocat.OutputSchemas.CheIsoRecord).execute()
    (getRecordResult.value.getXml \\ "CHE_MD_Metadata").head.prefix must_== "che"
  }
  def testOwn = {
    val getRecordResult = CswGetRecordById(importMetadataId, geocat.OutputSchemas.OwnRecord).execute()
    (getRecordResult.value.getXml \\ "CHE_MD_Metadata") must not beEmpty
  }
  def testGM03Url = {
    val getRecordResult = CswGetRecordById(importMetadataId, geocat.OutputSchemas.GM03UrlRecord).execute()
    (getRecordResult.value.getXml \\ "GM03_2Comprehensive.Comprehensive") must not beEmpty
  }
  def testGM03_2 = {
    val getRecordResult = CswGetRecordById(importMetadataId, geocat.OutputSchemas.GM03_2Record).execute()
    (getRecordResult.value.getXml \\ "GM03_2Comprehensive.Comprehensive") must not beEmpty
  }
  
  override lazy val importMetadataId = {
    val mdId = importMd(1, "/geocat/data/metadata.iso19139.che.xml", uuid.toString).head

    val md = GetRawMetadataXml.execute(mdId).value.getXml
    val fileId = (md \\ "fileIdentifier").text.trim
    fileId
  }

}