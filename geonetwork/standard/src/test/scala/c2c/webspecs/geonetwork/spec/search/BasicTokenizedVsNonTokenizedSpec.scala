package c2c.webspecs
package geonetwork
package spec.search
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._

@RunWith(classOf[JUnitRunner])
class BasicTokenizedVsNonTokenizedSpec extends GeonetworkSpecification with SearchSpecification with AbstractTokenizedVsNonTokenizedSpec[XmlSearchValues] {
  
   def search(params: (String, String)*) = {
    XmlSearch(params).to(10).execute().value.count
  }
}