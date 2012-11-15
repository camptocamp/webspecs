package c2c.webspecs
package geonetwork
package spec.csw.search

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step

import c2c.webspecs.geonetwork.csw._

@RunWith(classOf[JUnitRunner])
class CswGetRecordsSpec extends GeonetworkSpecification {
  def is =
    "A test for CSW GetRecords" ^ Step(setup) ^
      "First import a few metadata records" ^ doImport ^
      "A single search with ${hits} result type should find ${5} elements when maxRecords is ${5}" ! singleGetRecords ^
      "A single search with ${hits} result type should find ${2} elements when maxRecords is ${2}" ! singleGetRecords ^
      "A single search with ${hits} result type should find ${5} elements when maxRecords is ${100}" ! singleGetRecords ^
      "A single search with ${results} result type should find ${5} elements when maxRecords is ${5}" ! singleGetRecords ^
      "A single search with ${results_with_summary} result type should find ${5} elements when maxRecords is ${5}" ! singleGetRecords ^
      "Should be able to make several requests in parallel should not result in an error" ! multipleInParallel ^
      Step(tearDown)



  def metadataToImport = "/geonetwork/data/valid-metadata.iso19139.xml"
  def doImport = Step {
    importMd(5, metadataToImport, uuid.toString)
  }

  val singleGetRecords = (s:String) => {
    val (resultTypeName, expectedRecords, maxRecords) = extract3(s)
    val resultType = ResultTypes.withName(resultTypeName)
    val result = CswGetRecordsRequest(
      filter = PropertyIsEqualTo("abstract", uuid.toString).xml,
      maxRecords = maxRecords.toInt,
      resultType = resultType).execute()
      
  val data = result.value.getXml

    val expectedNumberOfMetadataElements = resultType match {
      case ResultTypes.hits =>
        (result.value.getXml \\ "SearchResults" \\ "MD_Metadata") must have size (0)
      case ResultTypes.results =>
        (result.value.getXml \\ "SearchResults" \\ "MD_Metadata") must have size (maxRecords.toInt)
      case ResultTypes.resultsWithSummary =>
        ((result.value.getXml \\ "SearchResults" \\ "MD_Metadata") must have size (maxRecords.toInt)) and
          ((result.value.getXml \\ "SearchResults" \\ "MD_Metadata") must \("info"))
    }
    (result must haveA200ResponseCode) and
      ((result.value.getXml \\ "SearchResults" \\ "@numberOfRecordsReturned").text must_== expectedRecords)
      ((result.value.getXml \\ "SearchResults" \\ "@numberOfRecordsMatched").text must_== "5") and
      expectedNumberOfMetadataElements
  }

  def multipleInParallel = {
    val requests = (1 to 5) map {
      page => CswGetRecordsRequest(PropertyIsEqualTo("abstract", uuid.toString).xml, maxRecords = 1, startPosition = page)
    }
    val responses = requests.par.map(_.execute()).seq
    responses must haveA200ResponseCode.forall
  }

}