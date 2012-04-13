package c2c.webspecs
package geonetwork
package spec.search

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.csw._
import scala.xml.NodeSeq

@RunWith(classOf[JUnitRunner])
class SummaryAccuracySpec extends GeonetworkSpecification { def is =
    "This spec verifies that the counts in the summary of a csw search " +
            "equals the number of hits when performing the search"          ^ Step(setup) ^
    testLanguages(languagesToTest)                                          ^ Step(tearDown)
  
  def languagesToTest = Seq("en", "fr", "de")
  def testLanguages(languages:Seq[String]) = {
    val fragments = languages.map{ lang => "Test "+lang+" translations" ^ testSummary(lang) }
    fragments.reduce(_ ^ _)
  }
  
  def testSummary(lang:String) = {
    val summaries = (summary(lang) \\ "_" filter (n => (n \ "@count").nonEmpty))
    val summaryFragments = summaries.map{
      element =>
        val searchTerm = (element \ "@name" text)
        val searchField = (element \ "@indexKey" text)
        val count = (element \ "@count" text)
        
        val desc = 
          if (element.label == "Summary") "Searching for all metadata should find "+count+" records"
          else "Summary for "+searchField+":"+searchTerm+" should find "+count+" records"
        desc ! findsCorrectNumberRecords(lang, searchField, searchTerm, count) 
    }
    val startFragment = "Perform csw search of all in ${"+lang+"} and obtain summary"  ^ Step(loadSummary)
    summaryFragments.foldLeft(startFragment)(_ ^ _) ^ end
  }

  def isNumeric(searchField:String) = searchField match {
    case "denominator" | "northBL" | "eastBL" | "southBL" | "westBL" => true
    case _ => false
  }

  def findsCorrectNumberRecords(lang:String, searchField:String, searchTerm:String, expectedResultCount:String) = {
    val query = searchField match {
      case "Summary" => Nil
      case field if isNumeric(field) => PropertyIsBetween(field, searchTerm.toInt, searchTerm.toInt).xml
      case _ => PropertyIsEqualTo(searchField, searchTerm).xml
    }
    
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

  var summaries = Map[String,NodeSeq]()
  def summary(lang:String) = {
    synchronized {
      summaries.getOrElse(lang,{
        val request = CswGetRecordsRequest(Nil, 
            resultType = ResultTypes.resultsWithSummary,
            maxRecords=1,
            url = lang+"/csw")
        val response = request.execute()
        assert(response.basicValue.responseCode == 200)
        val summary = response.value.getXml \\ "Summary"
        summaries += lang -> summary
        summary
      })
    }
  }

}