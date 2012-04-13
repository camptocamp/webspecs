package c2c.webspecs
package geonetwork
package geocat
package spec.WP15

import org.specs2.specification.Step
import c2c.webspecs.geonetwork.edit._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExtentXlinkAddSpec extends GeocatSpecification { def is =
  "Xlink Add Extent".title ^
  "This spec verifies that the html returned when performing an ajax xlink add extent the extent correctly been added" ^ Step(setup) ^
  "Open a metadata for editing"                                                 ^ Step(startEditing) ^
  "Verify that extent string is present in a input object" ! extentPresent ^
                                                              Step(tearDown)
                                                                  

  lazy val startEditing = {
    val id = importMd(1,"/geocat/data/bare.iso19139.che.xml", uuid.toString()).head
    val editValue = StartEditingHtml(MetadataViews.complete).execute(id).value
    val xlinks = editValue.getXml \\ "a"
    val addXlinkNode = xlinks find (n => (n @@ "id").headOption.exists(_ startsWith "addXlink_child_gmd:extent"))
    (addXlinkNode, id)
  }

  def extentHtml = {
    startEditing._1.map{n => 
      val onclickAtt = (n @@ "onclick" head)
      val args = onclickAtt.dropWhile(_ != '(').drop(1).takeWhile(_ != ')').split(",").map(_.trim)
      val elemRef = args(0)
      val request = GetRequest("metadata.xlink.add", 
          "id" -> startEditing._2, 
          "ref" -> elemRef, 
          "name" -> "gmd:extent", 
          "href" -> "local://xml.extent.get?id=351&wfs=default&typename=gn:gemeindenBB&format=gmd_complete&&extentTypeCode=true")
     (request.execute().value.getXml)
    }.get
  }
  def extentPresent = {
    val html = extentHtml
    val germanInput = html \\ "input" find (n => (n @@ "value" headOption) == Some("Bern"))
    
    germanInput must beSome
  }
  
}