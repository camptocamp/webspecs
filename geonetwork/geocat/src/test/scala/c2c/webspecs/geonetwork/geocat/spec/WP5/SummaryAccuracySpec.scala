package c2c.webspecs
package geonetwork
package geocat
package spec.WP5

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.csw._

@RunWith(classOf[JUnitRunner])
class SummaryAccuracySpec extends SearchSpecification { def is =
    "This spec verifies that the counts in the summary of a csw search " +
            "equals the number of hits when performing the search"          ^ Step(setup) ^
    "Test english translations" ^                                           testSummary("en") ^
    "Test french translations"  ^                                           testSummary("fr") ^
    "Test german translations"  ^                                           testSummary("de") ^
                                                                            Step(tearDown)
  def testSummary(lang:String) = {
    val summaries = (summary(lang) \\ "_" filter (n => (n \ "@count").nonEmpty)).filter(n => (n \ "@name" text) == "OGC:WMS")
    val summaryFragments = summaries.map{
      element =>
        val searchTerm = (element \ "@name" text)
        val searchField = element.label
        val count = (element \ "@count" text)
        
        val desc = 
          if (searchField == "Summary") "Searching for all metadata should find "+count+" records"
          else "Summary for "+searchField+":"+searchTerm+" should find "+count+" records"
        desc ! findsCorrectNumberRecords(lang, searchField, searchTerm, count) 
    }
    val startFragment = "Perform csw search of all in ${"+lang+"} and obtain summary"  ^ Step(loadSummary)
    summaryFragments.foldLeft(startFragment)(_ ^ _) ^ end
  }

  def findsCorrectNumberRecords(lang:String, searchField:String, searchTerm:String, expectedResultCount:String) = {
    val query = if(searchField == "Summary") Nil else PropertyIsEqualTo(searchField, searchTerm).xml
    val cswResponse = 
      CswGetRecordsRequest(
          query, 
        resultType = ResultTypes.hits,
        maxRecords=1,
        url = lang+"/csw").execute()
        
    val foundResults = (cswResponse.value.getXml \\ "SearchResults" \ "@numberOfRecordsMatched" text)

    (cswResponse must haveA200ResponseCode) and
        (foundResults aka "found results" must_== expectedResultCount)
  }
  
  def loadSummary = (s:String) => {
    val lang = extract1(s)
    (summary(lang) must not beEmpty) and
        ((summary(lang) \\ "_") must not beEmpty) 
  }

  lazy val enSummary = getSummary("eng")
  lazy val frSummary = getSummary("fra")
  lazy val deSummary = getSummary("deu")
  def getSummary(lang:String) = {
    val request = CswGetRecordsRequest(Nil, 
        resultType = ResultTypes.resultsWithSummary,
        maxRecords=1,
        url = lang+"/csw")
    val response = request.execute()
    assert(response.basicValue.responseCode == 200)
    response.value.getXml \\ "Summary"
  }
  def summary(lang:String) = lang match {
    case "en" => enSummary
    case "fr" => frSummary
    case "de" => deSummary
  }

}