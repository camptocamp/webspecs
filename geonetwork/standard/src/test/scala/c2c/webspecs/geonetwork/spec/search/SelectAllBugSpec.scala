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
    "SelectAllBugSpec".title ^ sequential ^ Step(setup)  ^
      "Import test data"  ^ Step(importData) ^ br ^
        "Search should return ${5} records" ! search ^
        "Select should select 5 records" ! select ^
        "Delete should execute correctly" ! delete ^
        "Search should now return ${0} records" ! search ^
        endp ^
      "Re-import test data" ^ Step(importData) ^
        "Perform a search with xml.search the search with the old search and no error should be loaded" ! noErrorBothSearches ^
        "${xml.search} then select-all should select all metadata records" ! selectAllTest ^
        "${embedded.search} then select-all should select all metadata records" ! selectAllTest ^
        "${csw.search} then select-all should select all metadata records" ! selectAllTest ^
      end ^ Step (tearDown)

  def importData = importMd(5,"/geonetwork/data/valid-metadata.iso19139.xml", datestamp)
  def select = {
    val result = SelectAll.execute().value.getXml
    (result \\ "Selected").text.trim must_== "5"
  }
  def delete = MetadataBatchDelete.execute() must haveA200ResponseCode
  val search = (s:String) => (searchRequest.execute().value.getXml \\ "summary" \ "@count").text must_== extract1(s)

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
    (SelectAll.execute().value.getXml \\ "Selected").text.trim.toInt must_== 5
  }

  val searchRequest = GetRequest("q", 'fast -> 'index, 'any -> ("Abstract "+datestamp))
  val embeddedRequest = GetRequest("main.search.embedded", 'any -> ("Abstract "+datestamp))
  val cswRequest = CswGetRecordsRequest(PropertyIsEqualTo("any", ("Abstract "+datestamp)).xml)

}