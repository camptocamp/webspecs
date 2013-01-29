package c2c.webspecs.geonetwork
package spec.search

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import c2c.webspecs.geonetwork.regions.Region

@RunWith(classOf[JUnitRunner])
class BasicSpatialSearchSpec extends GeonetworkSpecification with AbstractSpatialSearchSpec {

  override def titleExtension() = "Basic"
  override def search(relation:SpatialRelation, regions:Region*):Int = {
    val geom = "region:" + (regions.map(_.id).mkString(","))
    val results = XmlSearch().search("geometry" -> geom, 'relation -> relation.toString().toLowerCase()).execute().value
    results.count
  }

}