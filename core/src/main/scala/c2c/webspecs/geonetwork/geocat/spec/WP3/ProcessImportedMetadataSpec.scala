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
import scala.xml.NodeSeq

@RunWith(classOf[JUnitRunner]) 
class ProcessImportedMetadataSpec extends GeocatSpecification { def is =
  "Shared Object Processing of Imported Metadata".title ^ Step(setup) ^
  "This specification tests how imported metadata are processed for shared objects"             ^
      "When a ${data} metadata is imported"                                                     ^ importMetadata.toGiven ^
      "The import must complete successfully"                                                   ^ a200ResponseThen.narrow[TestData]  ^
      "All ${extent} must have ${several} xlink:href attributes"                                          ^ xlinked.toThen ^
      "All ${contact} must have ${several}  xlink:href attributes"                                          ^ xlinked.toThen ^
      "All ${descriptiveKeywords} must have ${several} xlink:href attributes"                              ^ xlinked.toThen ^
      "All ${citedResponsibleParty} must have ${several} xlink:href attributes"                            ^ xlinked.toThen ^
      "All ${pointOfContact} must have ${several} xlink:href attributes"                                   ^ xlinked.toThen ^
      "All ${resourceFormat} must have ${several} xlink:href attributes"                                   ^ xlinked.toThen ^
      "All ${userContactInfo} must have ${several} xlink:href attributes"                                  ^ xlinked.toThen ^
      "All ${distributionFormat} must have ${several} xlink:href attributes"                               ^ xlinked.toThen ^
      "All ${distributorContact} must have ${several} xlink:href attributes"                               ^ xlinked.toThen ^
      "All ${sourceExtent} must have ${1} xlink:href attributes"                                     ^ xlinked.toThen ^
      "All ${spatialExtent} must have ${2} xlink:href attributes"                                    ^ xlinked.toThen ^
                                                                                                  endp ^
                                                                                                  Step(tearDown)


   type TestData = Response[(NodeSeq,NodeSeq,NodeSeq)]

   val importMetadata:(String) => TestData = (s:String) => {
     val (xmlString,data) = extract1(s) match {
       case "data" =>
          ResourceLoader.loadDataFromClassPath("/geocat/data/comprehensive-iso19139che.xml",classOf[ProcessImportedMetadataSpec],uuid)
       case "service" =>
          ResourceLoader.loadDataFromClassPath("/geocat/data/wfs-service-metadata-template.xml",classOf[GeonetworkSpecification],uuid)
     }
    
     val originalXml = XML.loadString(xmlString)
     val ImportRequest = ImportMetadata.findGroupId(data,NONE,false)
     
     val importResponse = (UserLogin then ImportRequest)(None)
     val id = importResponse.value
     val mdWithXLinks =  GetEditingMetadataXml(id).value.getXml
     val mdWithoutXLinks = GetRawMetadataXml(id).value.getXml
     val deleteResponse = DeleteMetadata(id)
     assert(deleteResponse.basicValue.responseCode == 200, "Failed to delete imported metadata")

     importResponse.map(mv => (originalXml, mdWithoutXLinks, mdWithXLinks))
   }
   
   def xlinked = (r:TestData,s:String) => {
     val (_, _ , mdWithXLinks) = r.value
     val (node, number) = extract2(s)
     
     val elem = mdWithXLinks \\ node
     val countMatcher = if(number == "several") {
       (elem \@ "xlink:href" aka "href" must not beEmpty)
     } else {
       (elem \@ "xlink:href" aka "href" must haveSize(number.toInt) )
     }
     (elem aka (node+" element") must not beEmpty) and
     	countMatcher 
   }
   
}