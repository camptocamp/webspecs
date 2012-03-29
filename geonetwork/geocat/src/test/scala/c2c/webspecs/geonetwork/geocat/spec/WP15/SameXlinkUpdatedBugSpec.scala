package c2c.webspecs
package geonetwork
package geocat
package spec.WP15
import org.specs2.specification.Step
import edit._
import scala.xml._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SameXlinkUpdatedBugSpec extends GeonetworkSpecification { def is =
  "This spec verifies that if the same shared object is in a metadata the xml update" ^ Step(setup) ^
      "Import a metadata" ^ Step(importMetadata) ^
      "Do metadata.show with the same unchanged metadata" ^ Step(doUpdate) ^
      "Should have a 200 response" ! {doUpdate._2 must haveA200ResponseCode} ^
      "XML should be unchanged" ! unchangedMd ^ Step(tearDown)

  lazy val importMetadata = {
    importMd(1, "/geocat/data/metadata.iso19139.che.xml", uuid.toString).head
  }

  lazy val doUpdate = {
    val startEditing = StartEditingHtml(MetadataViews.xml).execute(importMetadata)
    val xml = XML.loadString((startEditing.value.getXml \\ "textarea").text)
    val updateResponse = UpdateMetadataHtml(true, false, 'data -> xml).execute(startEditing.value)
    (xml, updateResponse)
  }
  
  def unchangedMd = {
    val xlinksUpdated =  XML.loadString((doUpdate._2.value.getXml \\ "textarea").text) \\ "_" filter(_ @@ "xlink:href" nonEmpty)
    val xlinksBefore = doUpdate._1 \\ "_" filter(_ @@ "xlink:href" nonEmpty)
    def nodeNames(n:NodeSeq) = n \\ "_" map (_.label)
        
   ((nodeNames(xlinksUpdated) must_== nodeNames(xlinksBefore)) and
       (xlinksUpdated \\ "descriptiveKeywords" must haveSize(2)) and
       (xlinksUpdated \\ "CHE_CI_ResponsibleParty" must haveSize(1)) and
           (xlinksUpdated \\ "extent" must haveSize(1))).pendingUntilFixed
  }
}