package c2c.webspecs
package geonetwork
package spec.csw.search

import org.specs2.specification.Step
import scala.xml.Node
import csw._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import c2c.webspecs.geonetwork.spec.search.AbstractSearchOrderSpecSpecification

@RunWith(classOf[JUnitRunner])
class CswSearchOrderSpec extends GeonetworkSpecification  with AbstractSearchOrderSpecSpecification {
  def titleExtension = "Csw"
  def doSearch(lang:String) = {
    val cswResponse = CswGetRecordsRequest(
      PropertyIsEqualTo("abstract", timeStamp.toString).xml,
      ResultTypes.results,
      outputSchema = OutputSchemas.Record,
      elementSetName = ElementSetNames.summary,
      url = lang+"/csw",
      sortBy = List(SortBy("_title", true))).execute()
    cswResponse.value.getXml \\ "SummaryRecord" \\ "title" map (_.text)

  }
}