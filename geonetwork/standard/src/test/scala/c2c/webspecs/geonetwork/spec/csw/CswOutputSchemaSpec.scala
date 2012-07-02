package c2c.webspecs
package geonetwork
package spec.csw

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import OutputSchemas._
import c2c.webspecs.geonetwork.csw.CswGetCapabilities
import org.specs2.matcher.MatchResult
import org.specs2.execute.Result
import org.specs2.specification.Fragment
import org.specs2.specification.Fragments


@RunWith(classOf[JUnitRunner])
class CswOutputSchemaSpec extends GeonetworkSpecification {
  def is =
    "CSW output schemas".title ^ Step(setup) ^
      "This specification tests the usage of different output schemas through CSW" ^
      "must have ${http://www.opengis.net/cat/csw/2.0.2} as an outputSchema" ! outputSchema ^
      "must have ${http://www.isotc211.org/2005/gmd} as an outputSchema" ! outputSchema ^ 
      testCapabilitliesHasCustomOutputSchema ^ p ^
      "Import a metadata" ^ Step(importMetadataId) ^
      "Getting the metadata previously inserted in dublin-core output" ! testDublinCore ^
      "Getting the metadata previously inserted in iso19139 output" ! testiso19139 ^
      testCustomOutputSchemas ^
      end ^ Step(tearDown)


  lazy val getCapabilities = CswGetCapabilities().execute().value.getXml

  val outputSchema = (descriptor: String) => {
    val schema = extract1(descriptor)
    val getRecordsOperations = getCapabilities \\ "Operation" filter {
      _ @@ "name" == List("GetRecords")
    }
    val getOutputSchemas = (getRecordsOperations \\ "Parameter" filter {
      _ @@ "name" == List("outputSchema")
    }) \\ "Value"

    getOutputSchemas.map {
      _.text
    } must contain(schema)
  }

  def testCapabilitliesHasCustomOutputSchema:Fragments = success
  
  def testDublinCore = {
    // how to get the fileId of the inserted MD ?
    val getRecordResult = CswGetRecordById(importMetadataId, new OutputSchema("http://www.opengis.net/cat/csw/2.0.2") {}).execute()
    (getRecordResult.value.getXml \\ "title").head.prefix must_== "dc"
  }

  def testiso19139 = {
    val getRecordResult = CswGetRecordById(importMetadataId, new OutputSchema("http://www.isotc211.org/2005/gmd") {}).execute()
    (getRecordResult.value.getXml \\ "MD_Metadata").head.prefix must_== "gmd"

  }

  def testCustomOutputSchemas:Fragments = success
  
  lazy val importMetadataId = {
    val mdId = importMd(1, "/geonetwork/data/valid-metadata.iso19139.xml", uuid.toString).head

    val md = GetRawMetadataXml.execute(mdId).value.getXml
    val fileId = (md \\ "fileIdentifier").text.trim
    fileId
  }
}