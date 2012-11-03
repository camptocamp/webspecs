package c2c.webspecs
package geonetwork
package spec.regions

import regions._
import org.specs2.specification.Step

class GetRegionGeomSpec extends GeonetworkSpecification with AbstractRegionsSpec { def is =
  "This spec tests the regions get region functionality" ^ Step(setup) ^
      "The regions.geom.wkt service should return a single regions in wkt format" ! getWkt ^
      Step(tearDown)
      

   private def getWkt = {
      val region = WktGetRegionGeomRequest(regions1(0).id, regions1(0).categoryId).execute().value
      (region must contain("POLYGON"))
   }
}
