package c2c.webspecs
package geonetwork
package spec.search

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._

@RunWith(classOf[JUnitRunner])
class PagingBasicSearchSpec extends GeonetworkSpecification with SearchSpecification with AbstractPagingSearchSpec[XmlSearchValues] {

  def page(i: Int) = {
    val xmlResponse = XmlSearch(i,i+2, 'abstract -> (time + "NonSpatialSearchQuerySpec")).sortBy("date", false).execute().value

    val records = xmlResponse
    new {
      val codes = findCodesFromResults(xmlResponse)
      val nextRecord = records.to
      val recordsReturned = records.to - records.from
      val totalHits = records.size
    }
  }
}