package c2c.webspecs
package geonetwork
package spec.search
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BasicSearchOrderSpec extends GeonetworkSpecification with AbstractSearchOrderSpecSpecification {
  
  def doSearch(lang:String): Seq[String] = {
    implicit val fraresolver = new GeonetworkURIResolver() {
      override def locale = lang
    }
    val response = XmlSearch(1, 10,
      "abstract" -> timeStamp,
      "sortBy" -> "_title",
      'sortOrder -> 'reverse).execute()(context, fraresolver)
    val records = response.value.records

    records map (_.title)
  }

}