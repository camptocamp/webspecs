package c2c.webspecs
package geonetwork
package spec.search

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.csw._
import scala.xml.NodeSeq
import org.specs2.matcher.MustThrownExpectations
import org.specs2.matcher.MatchResult
import org.specs2.execute.Result

@RunWith(classOf[JUnitRunner])
class SummaryAccuracySpec extends GeonetworkSpecification with MustThrownExpectations{ def is =
    "This spec verifies that the counts in the summary of a csw search " +
    		"equals the number of hits when performing the search"          ^ Step(setup) ^
      "Add some data to ensure there is some data for searching"			^ Step(addData) ^ endp ^
    testLanguages(languagesToTest)                                          ^ Step(tearDown)
  
  def languagesToTest = Seq("eng", "fre", "ger")
  private def testLanguages(languages:Seq[String]) = {
    val fragments = languages.map{ lang => "Test "+lang+" translations" ^ testSummary(lang) ^ endp }
    fragments.reduce(_ ^ _)
  }

  def addData:Result = {
    config.adminLogin.execute();
    val result = AddSampleData("iso19139").execute() 
    result must haveA200ResponseCode
  }
  
  private def testSummary(lang:String) = {
    "Ensure summary request can be correctly made for "+lang ^ Step(summary(lang)) ^
    "For each summary element test that the correct result is obtained" ! verifyEachSummaryResult(lang)
  }

  private def verifyEachSummaryResult(lang: String) = {
    val summaries = (summary(lang) \\ "_" filter (n => (n \ "@count").nonEmpty && (n \ "@name").nonEmpty))
    val summaryFragments = summaries.map{
      element =>
        val searchTerm = (element \ "@name" text)
        val searchField = (element.label)
        val count = (element \ "@count" text)
        
        findsCorrectNumberRecords(lang, searchField, searchTerm, count) 
    }
    
    summaryFragments.foldLeft(success: Result){ _ and _ }
  }
  private def isNumeric(searchField:String) = searchField match {
    case "denominator" | "northBL" | "eastBL" | "southBL" | "westBL" => true
    case _ => false
  }

  private def findsCorrectNumberRecords(lang:String, searchField:String, searchTerm:String, expectedResultCount:String) = {
//    val query = searchField match {
//      case "Summary" => Nil
//      case field if isNumeric(field) => PropertyIsBetween(field, searchTerm.toInt, searchTerm.toInt).xml
//      case _ => PropertyIsEqualTo(searchField, searchTerm).xml
//    }
//    
//    val cswResponse = 
//      CswGetRecordsRequest(
//          query, 
//        resultType = ResultTypes.hits,
//        maxRecords=1,
//        url = lang+"/csw").execute()

    val query = searchField match {
      case "summary" => XmlSearch()
      case field if isNumeric(field) => XmlSearch().search(field -> searchTerm)
      case field => XmlSearch().search(field -> searchTerm)
    }

    implicit val resolver = new GeonetworkURIResolver() {
      override def locale = lang
    }
    val response = query.execute()(context, resolver)
    val foundResults = response.value.count

    (response must haveA200ResponseCode) and
        (foundResults aka "found results for "+query must_== expectedResultCount.toInt)//.pendingUntilFixed("Summary is not currently very accurate")
  }
  
  private def loadSummary = (s:String) => {
    val lang = extract1(s)
    (summary(lang) must not beEmpty) and
        ((summary(lang) \\ "_") must not beEmpty) 
  }

  private var summaries = Map[String,NodeSeq]()
  private def summary(lang:String) = {
    synchronized {
      summaries.getOrElse(lang,{
        
        val summary = XmlSearch().from(1).to(0).summaryOnly.execute().value.summary.get
//        val request = CswGetRecordsRequest(Nil, 
//            resultType = ResultTypes.resultsWithSummary,
//            maxRecords=1,
//            url = lang+"/csw")
//        val response = request.execute()
//        
//        response must haveA200ResponseCode
//        
//        val summary = response.value.getXml \\ "Summary"
        summaries += lang -> summary
        summary
      })
    }
  }

}