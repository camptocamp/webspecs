package c2c.webspecs
package geonetwork
package spec.regions

import regions._
import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ListRegionsSpec extends GeonetworkSpecification with AbstractRegionsSpec { def is =
  "This spec tests the regions list functionality" ^ Step(setup) ^
      "The regions.list.xml service should return an xml list of the regions" ! listXml ^
//      "The regions.list.json services should return a json list of the regions" ! listJson ^
      "The regions.list services should take a category parameter to limit the list" ! searchCategory ^
      "The regions.list services should take a label parameter to limit the list" ! searchName ^
      "The regions.list services should take a maxRecords parameter to limit the list" ! maxRecords ^
      Step(tearDown)
      
   private def listXml = {
      val regions = XmlListRegionsRequest().execute().value
      regions.map(_.id) must containAllOf( regions1Ids ++ regions2Ids)
   }
   def searchCategory = {
     val regions = XmlListRegionsRequest(categoryId = Some("country")).execute().value
     (regions.map(_.id) must containAllOf(regions1Ids)) and 
         (regions.map(_.id) must not (containAnyOf(regions2Ids)))
   }
   def searchName = {
     val region = regions1(0)
     val regions = XmlListRegionsRequest(label = Some(region.label.translations.head._2)).execute().value
     regions.map(_.id) must contain(region.id)
   }
   def maxRecords = {
     val regions = XmlListRegionsRequest(maxRecords = Some(1)).execute().value
     regions must haveSize(1)
   }
}
