package c2c.webspecs
package geonetwork
package spec.search.oldsearch

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import c2c.webspecs.geonetwork.spec.search.AbstractPagingSearchSpec
import scala.xml.NodeSeq

@RunWith(classOf[JUnitRunner])
class PagingOldSearchSpec extends GeonetworkSpecification with SearchSpecification with AbstractPagingSearchSpec[NodeSeq] {

  def page(startRecord: Int, endRecord: Option[Int]) = {
    val basicParams = Seq('from -> startRecord, 'sortOrder -> false, 'sortBy -> 'date,'abstract -> (time + "NonSpatialSearchQuerySpec"))
    val params = endRecord.map('to -> _).toSeq ++ basicParams
    val records = GetRequest("main.search.embedded", params : _*).execute().value.getXml

    val summary = super.summary(records)
    val allCodes = findCodesFromResults(records)
    new {
      val codes = allCodes
      val nextRecord = summary.endHits.toInt + 1
      val recordsReturned = allCodes.size
      val totalHits = summary.totalHits.toInt
    }
  }
  override def minimumRecordReturned = 1
}