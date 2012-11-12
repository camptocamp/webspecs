package c2c.webspecs
package geonetwork
package spec.regions

import regions._
import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GetRegionSpec extends GeonetworkSpecification with AbstractRegionsSpec { def is =
  "This spec tests the regions get functionality" ^ Step(setup) ^
      "The xml.regions.get service should return a single regions as xml" ! getXml ^
      "The xml.regions.get service should return a 404 error if the region does not exist" ! notFoundError ^
      Step(tearDown)

  private def notFoundError = {
    XmlGetRegionRequest("1").execute() must haveAResponseCode(404)
  }
  private def getXml = {
    val region = XmlGetRegionRequest(regions1(0).id).execute().value
    (region must beSome) and 
        (region.get.label.translations must contain(regions1(0).label.translations.head)) and
        (region.get.id must_== regions1(0).id) and
        (region.get.categoryId must_== regions1(0).categoryId) and
        (region.get.label.translations must containAllOf(regions1(0).label.translations.toSeq)) and
        (region.get.categoryLabel.translations must containAllOf(regions1(0).categoryLabel.translations.toSeq))
  }
}
