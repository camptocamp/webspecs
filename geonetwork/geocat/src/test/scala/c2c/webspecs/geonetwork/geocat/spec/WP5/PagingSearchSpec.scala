package c2c.webspecs
package geonetwork
package geocat
package spec.WP5

import csw._
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.ImportStyleSheets.NONE
import c2c.webspecs.geonetwork._
import c2c.webspecs.{ XmlValue, Response, IdValue, GetRequest }
import accumulating._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import scala.xml.transform.BasicTransformer
import c2c.webspecs.geonetwork.geocat.spec.WP3.ProcessImportedMetadataSpec
import scala.xml.Node
import scala.xml.XML
import scala.xml.Elem
import org.specs2.execute.Result
import java.util.Date
import org.specs2.matcher.ContainMatcher
import org.specs2.control.LazyParameters
import org.specs2.control.LazyParameter
import scala.xml.NodeSeq
import org.specs2.control.LazyParameter

@RunWith(classOf[JUnitRunner])
class PagingSearchSpec extends SearchSpecification {
  def is =
    "Non-spatial search queries".title ^
      "This specification tests how non-spatial search queries" ^ Step(setup) ^
      "First import several metadata that are to be searched for" ^ Step(importedMetadataId) ^
            ("Searching for '" + time + "NonSpatialSearchQuerySpec with a maxSize of 1 should return 1 " +
          		"record with a hits result of 4") ! checkFirstPage ^ 
            ("Searching for '" + time + "NonSpatialSearchQuerySpec' with a maxSize of 1 and a startPosition" +
          		" of 2 should return 1 record with a hits result of 4 and should not be the same page as in page 1") ! checkSecondPage ^ 
            ("Searching for '" + time + "NonSpatialSearchQuerySpec' with a maxSize of 1 and a startPosition" +
                    " of 5 should return 0 records with a hits result of 4") ! checkThirdPage ^ 
  Step(tearDown)

  def page(i:Int) = {
    val similarityProperty = PropertyIsEqualTo("similarity", "1")

    val filter = similarityProperty and PropertyIsEqualTo("abstract", time + "NonSpatialSearchQuerySpec")

    val xmlResponse = CswGetRecordsRequest(filter.xml,
      resultType = ResultTypes.resultsWithSummary,
      outputSchema = OutputSchemas.Record,
      maxRecords = 2,
      startPosition = i,
      sortBy = List(SortBy("date", false)))().value.getXml

    new { 
      val codes = findCodesFromResults(xmlResponse)
      val xml = xmlResponse
      val totalHits =  (xmlResponse \\ "@numberOfRecordsMatched").text.toInt
      val recordsReturned = (xmlResponse \\ "@numberOfRecordsReturned").text.toInt
      val nextRecord = (xmlResponse \\ "@nextRecord").text.toInt
    }
  }
  lazy val firstPage = page(1)
  lazy val secondPage = page(3)
  
  def checkFirstPage = {
    (firstPage.codes must haveSize (2)) and
        (firstPage.nextRecord must_== 3) and
        (firstPage.recordsReturned must_== 2) and
        (firstPage.totalHits must_== 4)
  }

  def checkSecondPage = {
    (secondPage.codes must haveSize (2)) and
      (secondPage.nextRecord must_== 5) and
      (secondPage.recordsReturned must_== 2) and
      (secondPage.totalHits must_== 4) and 
      (secondPage.codes must not contain(firstPage.codes(0), firstPage.codes(1)))
  }
  
  def checkThirdPage = {
    val thirdPage = page(5)
  (thirdPage.codes must haveSize (0)) and
      (thirdPage.recordsReturned must_== 0) and
      (thirdPage.totalHits must_== 4)
  }
  
}