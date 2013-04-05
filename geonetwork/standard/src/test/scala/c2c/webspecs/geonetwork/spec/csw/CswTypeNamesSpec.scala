package c2c.webspecs
package geonetwork
package spec.csw

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step

import c2c.webspecs.geonetwork.csw._
import c2c.webspecs.geonetwork.GetRawMetadataXml

@RunWith(classOf[JUnitRunner])
class CswTypeNamesSpec extends GeonetworkSpecification {
  def is = {
    "Import a metadata" ^ Step(setup) ^ Step(importMetadataId) ^
      "Make request with multiple typenames, the metadata should be returned" ! request ^
      "Make request with multiple typenames, on a csw endpoint" ! requestCswEndpoint ^
      Step(tearDown)
  }

  lazy val importMetadataId = {
    val mdId = importMd(1, "/geonetwork/data/multilingual-metadata.iso19139.xml", uuid.toString).head

    val md = GetRawMetadataXml.execute(mdId).value.getXml
    val response = (md \\ "fileIdentifier").text.trim
    response
  }

  def request = {
    val results = CswGetRecordsRequest(Nil, typeNames = List("csw:Record", "gmd:MD_Metadata")).execute()
    println(results.value.getXml)
    val matches = (results.value.getXml \ "SearchResults" \ "@numberOfRecordsMatched")
    (matches must not(beEmpty)) and (matches.text.toInt must_== 1)
  }

  def requestCswEndpoint = {
    val results = CswGetRecordsRequest(Nil, url="csw-integration-test", typeNames = List("csw:Record", "gmd:MD_Metadata")).execute()
    println(results.value.getXml)
    val matches = (results.value.getXml \ "SearchResults" \ "@numberOfRecordsMatched")
    (matches must not(beEmpty)) and (matches.text.toInt must_== 1)
  }
}