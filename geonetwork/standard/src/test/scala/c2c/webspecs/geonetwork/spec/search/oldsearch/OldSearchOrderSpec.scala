package c2c.webspecs
package geonetwork
package spec.search.oldsearch
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import c2c.webspecs.geonetwork.spec.search.AbstractSearchOrderSpecSpecification

@RunWith(classOf[JUnitRunner])
class OldSearchOrderSpec extends GeonetworkSpecification with AbstractSearchOrderSpecSpecification with OldSearchSpecificationSupport {
  def titleExtension = "Old"
  def doSearch(lang:String): Seq[String] = {
    implicit val fraresolver = new GeonetworkURIResolver() {
      override def locale = lang
    }
    val response = GetRequest("main.search.embedded", 'abstract -> timeStamp, 'sortOrder -> true, 'sortBy -> "_title").execute()(context, fraresolver).value.getXml
    val records = (hits(response) \\ "div").filter(div => (div @@ "class") == List("hittitle")).map(_.text.trim) 

    records
  }

}