package c2c.webspecs.geonetwork.spec.search

import c2c.webspecs._
import geonetwork._
import csw._

import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner


/**
 * User: jeichar
 * Date: 1/19/12
 * Time: 10:05 AM
 */
@RunWith(classOf[JUnitRunner])
class SelectAllBugSpec extends GeonetworkSpecification {
  def is =
    "SelectAllBugSpec".title ^ Step(setup) ^
      "Import test data"  ^ Step(importData) ^ br ^
        "Perform a search with xml.search; select all; batch delete" ^ Step(search) ^
        "Search should no longer return the test data" ! noTestData ^
        endp ^
      "Re-import test data" ^ Step(importData) ^
        "Perform a search with xml.search the search with the old search and no error should be loaded" ! noErrorBothSearches ^
        "${xml.search} then select-all should select all metadata records" ! selectAllTest ^
        "${embedded.search} then select-all should select all metadata records" ! selectAllTest ^
        "${csw.search} then select-all should select all metadata records" ! selectAllTest ^
      end ^ Step (tearDown)

  def importData = importMd(5,"/geonetwork/data/valid-metadata.iso19139.xml", datestamp)

  def search = {
    val getResult = searchRequest.execute()
    val selectResult = SelectAll.execute()
    val deleteResult = MetadataBatchDelete.execute()

    val foundCount = (getResult.value.getXml \\ "response" \\ "@count").text
    val selectedCount = (selectResult.value.getXml \\ "Selected").text.trim

    (List(getResult, selectResult, deleteResult) must haveA200ResponseCode.forall) and
      (foundCount must_== "5") and
      (selectedCount must_== "5")
  }

  def noTestData =
    (searchRequest.execute().value.getXml \\ "response" \\ "@count").text must_== "0"

  def noErrorBothSearches = {
    val xmlSearchResult = searchRequest.execute()
    val embeddedSearch = embeddedRequest.execute()

    List(xmlSearchResult,embeddedSearch) must haveA200ResponseCode.forall
  }
  
  val selectAllTest = (s:String) => {
    val request = extract1(s) match {
      case "xml.search" => searchRequest
      case "embedded.search" => embeddedRequest
      case "csw.search" => cswRequest
    }
    
    request.execute()
    (SelectAll.execute().value.getXml \\ "Selected").text.trim must_== 5
  }


  val searchRequest = GetRequest("q", 'fast -> 'index, 'fileId -> datestamp)
  val embeddedRequest = GetRequest("main.search.embedded", 'fileId -> datestamp)
  val cswRequest = CswGetRecordsRequest(PropertyIsEqualTo("fileId", datestamp).xml)

}