package c2c.webspecs
package geonetwork
package geocat
package spec.WP5

import csw._
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.ImportStyleSheets.NONE
import c2c.webspecs.geonetwork._
import c2c.webspecs.{ XmlValue, Response, IdValue, GetRequest }
import accumulating._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import scala.xml.transform.BasicTransformer
import c2c.webspecs.geonetwork.geocat.spec.WP3.ProcessImportedMetadataSpec
import scala.xml.Node
import scala.xml.XML
import scala.xml.Elem
import org.specs2.execute.Result
import java.util.Date
import org.specs2.matcher.ContainMatcher
import org.specs2.control.LazyParameters
import org.specs2.control.LazyParameter
import scala.xml.NodeSeq
import org.specs2.control.LazyParameter

@RunWith(classOf[JUnitRunner])
class DifferentLanguageSearchSpec extends SearchSpecification { def is =
    "Different language searches" ^ Step(setup) ^
    "Import a metadata" ^ Step(importMd) ^
    "Assert that the metadata is found when searching in ${eng}" ! search ^
    "Assert that the metadata is found when searching in ${fra}" ! search ^
    "Assert that the metadata is found when searching in ${deu}" ! search ^
    "Assert that the metadata is found when searching in ${ita}" ! search ^
                                                                   Step(tearDown)
                                                                   
 def importMd = {
    val importRequest = ImportMetadata.defaults(uuid,"/geocat/data/bare.iso19139.che.xml",false,getClass,ImportStyleSheets.NONE)._2
    
    1 to 2 foreach {_ => registerNewMd(Id(importRequest().value.id))}
  }

  def search = (string:String) => {
    val lang = extract1(string)
    val xml = CswGetRecordsRequest(PropertyIsEqualTo("AnyText","Title"+uuid).xml, url=lang+"/csw")().value.getXml
    
    (xml \\ "@numberOfRecordsMatched").text.toInt must_== 2
  }
  
}