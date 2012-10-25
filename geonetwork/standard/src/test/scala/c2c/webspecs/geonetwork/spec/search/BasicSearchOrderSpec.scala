package c2c.webspecs
package geonetwork
package spec.search
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BasicSearchOrderSpec extends GeonetworkSpecification with AbstractSearchOrderSpecSpecification {
  def titleExtension = "Basic"
  def doSearch(lang:String): Seq[String] = {
    implicit val fraresolver = new GeonetworkURIResolver() {
      override def locale = lang
    }
    val response = XmlSearch().to(10).search(
      'abstract -> timeStamp).sortBy("_title", true).execute()(context, fraresolver)
    val records = response.value.records

    records map (_.title)
  }

}