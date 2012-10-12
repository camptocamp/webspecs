package c2c.webspecs
package geonetwork
package spec.search
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._

@RunWith(classOf[JUnitRunner])
class DifferentLanguageBasicSearchSpec extends GeonetworkSpecification with SearchSpecification with AbstractDifferentLanguageSearchSpec[XmlSearchValues] {
  
  def search = (string:String) => {
    val lang = extract1(string)
    val results = XmlSearch(1, 10, 'any -> ("Title"+datestamp)).execute().value

    results.size must_== 2
  }
}