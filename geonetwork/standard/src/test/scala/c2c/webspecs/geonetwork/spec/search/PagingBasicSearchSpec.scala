package c2c.webspecs
package geonetwork
package spec.search

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._

@RunWith(classOf[JUnitRunner])
class PagingBasicSearchSpec extends GeonetworkSpecification with SearchSpecification {
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

  def page(i: Int) = {
    val xmlResponse = XmlSearch(2, 'similarity -> 1, 'abstract -> (time + "NonSpatialSearchQuerySpec")).sortBy("date", false).from(i).execute().value

    new {
      val codes = findCodesFromResults(xmlResponse)
      val records = xmlResponse
    }
  }
  lazy val firstPage = page(1)
  lazy val secondPage = page(3)

  def checkFirstPage = {
    (firstPage.codes must haveSize(2)) and
      (firstPage.records.to must_== 3) and
      (firstPage.records.to - firstPage.records.from must_== 2) and
      (firstPage.records.size must_== 4)
  }

  def checkSecondPage = {
    (secondPage.codes must haveSize(2)) and
      (secondPage.records.to must_== 5) and
      (secondPage.records.to - secondPage.records.from must_== 2) and
      (secondPage.records.size must_== 4) and
      (secondPage.codes must not contain (firstPage.codes(0), firstPage.codes(1)))
  }

  def checkThirdPage = {
    val thirdPage = page(5)
    (thirdPage.codes must haveSize(0)) and
      (thirdPage.records.to - thirdPage.records.from must_== 0) and
      (thirdPage.records.size must_== 4)
  }

}