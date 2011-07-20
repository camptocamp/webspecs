package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step
import ImportStyleSheets._
import scala.xml.transform.BasicTransformer
import scala.xml.Node
import scala.xml.XML
import scala.xml.Elem
import scala.xml.MetaData
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner]) 
class ProcessImportedMetadataSpec extends GeocatSpecification { def is =
  "Shared Object Processing of Imported Metadata".title ^ Step(setup) ^
  "This specification tests how imported metadata are processed for shared objects"             ^
      "When a ${data} metadata is imported"                                                     ^ importMetadata.toGiven ^
      "The import must complete successfully"                                                   ^ a200ResponseThen.narrow[Response[(Node,Node)]]  ^
      "All ${extents} must have xlink:href attributes"                                          ^ xlinked.toThen ^
      "All ${contact} must have xlink:href attributes"                                          ^ xlinked.toThen ^
      "All ${descriptiveKeywords} must have xlink:href attributes"                              ^ xlinked.toThen ^
      "All ${citedResponsibleParty} must have xlink:href attributes"                            ^ xlinked.toThen ^
      "All ${pointOfContact} must have xlink:href attributes"                                   ^ xlinked.toThen ^
      "All ${resourceFormat} must have xlink:href attributes"                                   ^ xlinked.toThen ^
      "All ${userContactInfo} must have xlink:href attributes"                                  ^ xlinked.toThen ^
      "All ${distributionFormat} must have xlink:href attributes"                               ^ xlinked.toThen ^
      "All ${distributorContact} must have xlink:href attributes"                               ^ xlinked.toThen ^
      "All ${sourceExtent} must have xlink:href attributes"                                     ^ xlinked.toThen ^
      "All ${spatialExtent} must have xlink:href attributes"                                    ^ xlinked.toThen ^
      "The imported metadata must have same elements as original data (in this case)"           ^ sameElements.toThen ^
                                                                                                  endp ^
                                                                                                  Step(tearDown)


                              

   val importMetadata:(String) => Response[(Node,Node)] = (s:String) => {
     val (xmlString,data) = extract1(s) match {
       case "data" =>
          ResourceLoader.loadDataFromClassPath("/geocat/data/comprehensive-iso19139che.xml",classOf[ProcessImportedMetadataSpec],uuid)
       case "service" =>
          ResourceLoader.loadDataFromClassPath("/geocat/data/wfs-service-metadata-template.xml",classOf[GeonetworkSpecification],uuid)
     }
    
     val originalXml = XML.loadString(xmlString)
     val ImportRequest = ImportMetadata.findGroupId(data,NONE,false)
     
     val response = (UserLogin then ImportRequest then GetEditingMetadataXml startTrackingThen DeleteMetadata)(None)

     response._1.map(mv => (originalXml, mv.getXml.asInstanceOf[Node]))
   }
 
   val sameElements = (r:Response[(Node,Node)]) => {
     val (originalMd, importedMd) = r.value
     val noxlinks = RemoveHrefs.apply(importedMd)
     noxlinks must beEqualToIgnoringSpace(originalMd)
   }
   
   val xlinked = (r:Response[(Node,Node)],s:String) => {
     val (_, importedMd) = r.value
     val node = extract1(s)
     
     importedMd \\ node \@ "xlink:href" must not beEmpty
   }
   
   object RemoveHrefs extends BasicTransformer {
     override def transform(n:Node) = n match {
       case e:Elem =>
         val xlinklessAtts = e.attributes filterNot {_.key startsWith "xlink:"} reduce {(acc,next) => acc.copy(next)}
         e.copy(attributes = xlinklessAtts)
       case _ => 
         n
     }
   }
}