package c2c.webspecs
package geonetwork
package spec.regions

import regions._
import org.specs2.specification.Step

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GetRegionGeomSpec extends GeonetworkSpecification with AbstractRegionsSpec { def is =
  "This spec tests the regions get region functionality" ^ Step(setup) ^
      "The regions.geom.wkt service should return a single regions in wkt format" ! getWkt ^
      Step(tearDown)
      

   private def getWkt = {
      val region = WktGetRegionGeomRequest(regions1(0).id).execute().value
      (region must contain("POLYGON"))
   }
}
