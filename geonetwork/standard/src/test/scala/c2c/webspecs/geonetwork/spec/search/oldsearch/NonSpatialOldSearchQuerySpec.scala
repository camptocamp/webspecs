package c2c.webspecs
package geonetwork
package spec.search.oldsearch

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import scala.xml.NodeSeq
import c2c.webspecs.geonetwork.spec.search.AbstractNonSpatialSearchQuerySpec

@RunWith(classOf[JUnitRunner])
class NonSpatialOldSearchQuerySpec extends GeonetworkSpecification with SearchSpecification with AbstractNonSpatialSearchQuerySpec[NodeSeq] {
  
  override def searchRequest(maxRecords: Int, sortByField: Option[(String, Boolean)], properties: (Double, String, String)*) = {
    val filter = properties.toList.flatMap(p => List("similarity" -> p._1, p._2 -> p._3))
    val sortBy = sortByField.map{_ match {
      case (field, true) => List("sortBy" -> field)
      case (field, false) => List("sortBy" -> field, "sortOrder" -> 'reverse)
    }}.getOrElse (Nil)
    
    val sortAndFilter = sortBy ::: filter
    GetRequest("main.search.embedded", (('to -> maxRecords) +: sortAndFilter): _*).map(_.getXml)
  }
}