package c2c.webspecs
package geonetwork
package spec.search
import org.specs2.specification.Step

trait AbstractPagingSearchSpec[SearchResult] {
  self: GeonetworkSpecification with AbstractSearchSpecification[SearchResult] =>
  def titleExtension:String
  def is =
    ("PagingSearch"+titleExtension).title ^
      "This specification tests how non-spatial search queries" ^ Step(setup) ^
      "First import several metadata that are to be searched for" ^ Step(importedMetadataId) ^
            ("Searching for '" + time + "NonSpatialSearchQuerySpec with a maxSize of 1 should return 1 " +
          		"record with a hits result of 4") ! checkFirstPage ^ 
            ("Searching for '" + time + "NonSpatialSearchQuerySpec' with a maxSize of 1 and a startPosition" +
          		" of 2 should return 1 record with a hits result of 4 and should not be the same page as in page 1") ! checkSecondPage ^ 
            ("Searching for '" + time + "NonSpatialSearchQuerySpec' with a maxSize of 1 and a startPosition" +
                    " of 5 should return 0 records with a hits result of 4") ! checkThirdPage ^
            ("Searching for '" + time + "NonSpatialSearchQuerySpec' with a maxSize of 1 and a startPosition" +
                    " of 1 (no to value) should return 3 records with a hits result of 4") ! checkAll ^
  Step(tearDown)

  def minimumRecordReturned = 0
  /**
   * make a search request with the first record being "startRecord".  The search should be limited to 2 records
   */
  def page(startRecord: Int, endRecord: Option[Int]): {
    def codes: Seq[String]
    def nextRecord: Int
    def recordsReturned: Int
    def totalHits: Int
  }

  lazy val firstPage = page(1,Some(2))
  lazy val secondPage = page(3,Some(4))

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
    val thirdPage = page(5, Some(6))
    (thirdPage.codes must haveSize(minimumRecordReturned)) and
      (thirdPage.recordsReturned must_== minimumRecordReturned) and
      (thirdPage.totalHits must_== 4)
  }
  
  
  def checkAll = {
      val all = page(1, None)
      (all.codes must haveSize(4)) and
      (all.recordsReturned must_== 4) and
      (all.totalHits must_== 4)
  }
  

}