package c2c.webspecs
package geonetwork
package spec.csw.search

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import c2c.webspecs.geonetwork.spec.search.AbstractPagingSearchSpec

@RunWith(classOf[JUnitRunner])
class PagingCswSearchSpec extends GeonetworkSpecification with SearchSpecification with AbstractPagingSearchSpec[XmlValue] {
   def page(startRecord: Int, endRecord: Option[Int]) = {

    val xmlResponse = CswGetRecordsRequest(Nil,
      resultType = ResultTypes.resultsWithSummary,
      outputSchema = OutputSchemas.Record,
      maxRecords = endRecord.getOrElse(1000) - startRecord + 1,
      startPosition = startRecord,
      sortBy = List(SortBy("date", false))).execute().value

    val xml = xmlResponse

    new {
      val codes = findCodesFromResults(xmlResponse)
      val totalHits = (xmlResponse.getXml \\ "@numberOfRecordsMatched").text.toInt
      val recordsReturned = (xmlResponse.getXml \\ "@numberOfRecordsReturned").text.toInt
      val nextRecord = (xmlResponse.getXml \\ "@nextRecord").text.toInt
    }
  }
  
}