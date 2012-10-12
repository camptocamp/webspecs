package c2c.webspecs
package geonetwork
package spec.csw.search

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import c2c.webspecs.geonetwork.spec.search.AbstractDifferentLanguageSearchSpec

@RunWith(classOf[JUnitRunner])
class DifferentLanguageCswSearchSpec extends GeonetworkSpecification with SearchSpecification with AbstractDifferentLanguageSearchSpec[XmlValue] { 
                                                                   
  def search = (string:String) => {
    val lang = extract1(string)
    val xml = CswGetRecordsRequest(PropertyIsEqualTo("AnyText","Title"+datestamp).xml, url=lang+"/csw").execute().value.getXml
    
    (xml \\ "@numberOfRecordsMatched").text.toInt must_== 2
  }
  
}