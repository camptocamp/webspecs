package c2c.webspecs
package geonetwork
package spec.search
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._

@RunWith(classOf[JUnitRunner])
class DifferentLanguageBasicSearchSpec extends GeonetworkSpecification with SearchSpecification { def is =
    "Different language searches" ^ Step(setup) ^
    "Import a metadata" ^ Step(importExtraMd(2, identifier=datestamp)) ^
    "Assert that the metadata is found when searching in ${eng}" ! search ^
    "Assert that the metadata is found when searching in ${fra}" ! search ^
    "Assert that the metadata is found when searching in ${deu}" ! search ^
    "Assert that the metadata is found when searching in ${ita}" ! search ^
                                                                   Step(tearDown)
                                                                   
  def search = (string:String) => {
    val lang = extract1(string)
    val results = XmlSearch(10, 'any -> ("Title"+datestamp)).execute().value

    results.size must_== 2
  }
  
}