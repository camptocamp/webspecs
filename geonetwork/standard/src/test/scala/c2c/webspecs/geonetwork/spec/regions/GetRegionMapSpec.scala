package c2c.webspecs
package geonetwork
package spec.regions

import regions._
import org.specs2.specification.Step

class GetRegionMapSpec extends GeonetworkSpecification with AbstractRegionsSpec { def is =
  "This spec tests the regions get map functionality" ^ Step(setup) ^
      "The regions.geom.wkt service should return a single regions in wkt format" ! getWkt ^
      Step(tearDown)
      

   private def getWkt = {
      val region = RegionGetMapRequest(regions1(0).id, regions1(0).categoryId).execute().value
      region.size must be_> (1)
   }
}
