package c2c.webspecs
package geonetwork
package spec.csw.search
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import c2c.webspecs.geonetwork.spec.search.AbstractTokenizedVsNonTokenizedSpec

@RunWith(classOf[JUnitRunner])
class CswTokenizedVsNonTokenizedSpec extends GeonetworkSpecification with SearchSpecification with AbstractTokenizedVsNonTokenizedSpec[XmlValue] {
  
   def search(params: (String, String)*) = {
     val first: OgcFilter = PropertyIsEqualTo(params.head._1, params.head._2)
    val filter = params.tail.foldLeft(first)((acc, next) => acc.and(PropertyIsEqualTo(next._1, next._2))) 
    val xml = CswGetRecordsRequest(filter.xml).execute().value.getXml
    
    (xml \\ "@numberOfRecordsMatched").text.toInt

  }
}