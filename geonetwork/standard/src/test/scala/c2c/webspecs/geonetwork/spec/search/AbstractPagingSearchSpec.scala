package c2c.webspecs
package geonetwork
package spec.search
import org.specs2.specification.Step

trait AbstractPagingSearchSpec[SearchResult] {
  self: GeonetworkSpecification with AbstractSearchSpecification[SearchResult] =>

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

  def page(i: Int): {
    def codes: Seq[String]
    def nextRecord: Int
    def recordsReturned: Int
    def totalHits: Int
  }

  lazy val firstPage = page(1)
  lazy val secondPage = page(3)

  def checkFirstPage = {
    (firstPage.codes must haveSize(2)) and
      (firstPage.nextRecord must_== 3) and
      (firstPage.recordsReturned must_== 2) and
      (firstPage.totalHits must_== 4)
  }

  def checkSecondPage = {
    (secondPage.codes must haveSize(2)) and
      (secondPage.nextRecord must_== 5) and
      (secondPage.recordsReturned must_== 2) and
      (secondPage.totalHits must_== 4) and
      (secondPage.codes must not contain (firstPage.codes(0), firstPage.codes(1)))
  }

  def checkThirdPage = {
    val thirdPage = page(5)
    (thirdPage.codes must haveSize(0)) and
      (thirdPage.recordsReturned must_== 0) and
      (thirdPage.totalHits must_== 4)
  }
  

}