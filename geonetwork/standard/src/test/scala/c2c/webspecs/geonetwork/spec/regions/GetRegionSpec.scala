package c2c.webspecs
package geonetwork
package spec.regions

import regions._
import org.specs2.specification.Step

class GetRegionSpec extends GeonetworkSpecification with AbstractRegionsSpec { def is =
  "This spec tests the regions get functionality" ^ Step(setup) ^
      "The regions.get.xml service should return a single regions as xml" ! getXml ^
      Step(tearDown)
      

   private def getXml = {
      val region = XmlGetRegionRequest(regions1(0).id, regions1(0).categoryId).execute().value
      (region must haveSize(1))
   }
}
