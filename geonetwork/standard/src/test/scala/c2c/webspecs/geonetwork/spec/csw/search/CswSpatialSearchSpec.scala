package c2c.webspecs.geonetwork
package spec.csw.search

import csw._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import c2c.webspecs.geonetwork.spec.search.AbstractSpatialSearchSpec
import c2c.webspecs.geonetwork.regions.Region

@RunWith(classOf[JUnitRunner])
class CswSpatialSearchSpec extends GeonetworkSpecification with AbstractSpatialSearchSpec {

  override def titleExtension() = "CSW"
  override def search(relation:SpatialRelation, regions:Region*):Int = {
    val filter = csw.Within((regions.map(_.id).mkString(",")))
    val results = CswGetRecordsRequest(filter.xml).execute().value
    val resultXml = results.getXml
    (resultXml \\ "@numberOfRecordsMatched").text.toInt
  }

}