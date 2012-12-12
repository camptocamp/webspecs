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
    implicit val resolver = new GeonetworkURIResolver() {
      override def locale = lang
    }
    val results = XmlSearch().to(10).search('any -> ("Title"+datestamp)).execute()(context, resolver).value

    results.size must_== 2
  }
}