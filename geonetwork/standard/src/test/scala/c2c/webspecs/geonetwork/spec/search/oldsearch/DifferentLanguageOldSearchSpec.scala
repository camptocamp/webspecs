package c2c.webspecs
package geonetwork
package spec.search.oldsearch
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import c2c.webspecs.geonetwork.spec.search.AbstractDifferentLanguageSearchSpec
import scala.xml.NodeSeq

@RunWith(classOf[JUnitRunner])
class DifferentLanguageOldSearchSpec extends GeonetworkSpecification with SearchSpecification with AbstractDifferentLanguageSearchSpec[NodeSeq] {
  
  def search = (string:String) => {
    val lang = extract1(string)
    val results =  GetRequest("main.search.embedded",'any -> ("Title"+datestamp), 'to -> 10).execute().value.getXml

    hits(results).size must_== 2
  }
}