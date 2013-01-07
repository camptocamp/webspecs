package c2c.webspecs
package geonetwork
package geocat
package spec.WP4

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import c2c.webspecs.Response
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import c2c.webspecs.geonetwork.geocat.spec.WP3.ProcessImportedMetadataSpec
import scala.xml.Node
import scala.xml.XML
import org.specs2.execute.Result


// TODO : finish to implement this test ...


@RunWith(classOf[JUnitRunner]) 
class TestXlinksUpdate extends GeocatSpecification {  def is =

  "This specification tests how xlinks are correctly processed"             					  ^ Step(setup)               ^
      "When a ${data} metadata is imported"                                                       ^ importMetadata.toGiven ^
      "The import must complete successfully"                                                     ^ a200ResponseThen.narrow[Response[(Node,Node)]]  ^
      "All ${contact} xlinks must have been resolved and therefore have children"			  	  ^ xlinked.toThen ^
      "All ${descriptiveKeywords} must have been resolved and therefore have children"			  ^ xlinked.toThen ^
      "All ${citedResponsibleParty} must have been resolved and therefore have children"	 	  ^ xlinked.toThen ^
      "All ${pointOfContact} must have been resolved and therefore have children"			  	  ^ xlinked.toThen ^
      "All ${resourceFormat} must have been resolved and therefore have children"			  	  ^ xlinked.toThen ^
      "All ${userContactInfo} must have been resolved and therefore have children"			      ^ xlinked.toThen ^
      "All ${distributionFormat} must have been resolved and therefore have children"			  ^ xlinked.toThen ^
      "All ${distributorContact} must have been resolved and therefore have children"			  ^ xlinked.toThen ^
                                                                                                  Step(tearDown)


                              

   val importMetadata:(String) => Response[(Node,Node)] = (s:String) => {
     val name = extract1(s) match {
       case "data" => "comprehensive-iso19139che.xml"
       case "service" => "wfs-service-metadata-template.xml"
     }
    val (xmlString,importRequest) = ImportMetadata.defaults(uuid, "/geocat/data/"+name,false,classOf[ProcessImportedMetadataSpec])
    
     val originalXml = XML.loadString(xmlString)
     
     val response = (UserLogin then importRequest then GetEditingMetadataXml).execute()
     DeleteMetadata.execute(response.value)

     response.map(mv => (originalXml, mv.getXml.asInstanceOf[Node]))
   }
 
   val xlinked = (r:Response[(Node,Node)],s:String) => {
     val (_, importedMd) = r.value
     val node = extract1(s)
     val hrefs = importedMd \\ node filter (_ @@ "xlink:href" nonEmpty)
     
     val haveChildren = (hrefs foldLeft (success:Result)) {(result,next) =>
       result and (next.child must not beEmpty)
     }

     haveChildren and (hrefs must not beEmpty) 
   }
}