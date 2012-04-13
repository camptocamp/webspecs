package c2c.webspecs
package geonetwork
package geocat
package spec.WP15

import org.specs2.specification.Step
import c2c.webspecs.geonetwork.edit._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class KeywordsXlinkAddSpec extends GeocatSpecification { def is =
  "Xlink Add Keyword".title ^
  "This spec verifies that the html returned when performing an ajax xlink add keyword has keyword correctly" ^ Step(setup) ^
  "Open a metadata for editing"                                                 ^ Step(startEditing) ^
  "Verify that keyword string is present in a input object" ! keywordPresent ^
                                                              Step(tearDown)
                                                                  

  lazy val startEditing = {
    val id = importMd(1,"/geocat/data/bare.iso19139.che.xml", uuid.toString()).head
    val editValue = StartEditingHtml(MetadataViews.ISOCore).execute(id).value
    val xlinks = editValue.getXml \\ "a"
    val addXlinkNode = xlinks find (n => (n @@ "id").headOption.exists(_ startsWith "addXlink_child_gmd:descriptiveKeywords"))
    (addXlinkNode, id)
  }

  def keywordHtml = {
    startEditing._1.map{n => 
      val onclickAtt = (n @@ "onclick" head)
      val args = onclickAtt.dropWhile(_ != '(').drop(1).takeWhile(_ != ')').split(",").map(_.trim)
      val elemRef = args(0)
      val request = GetRequest("metadata.xlink.add", 
          "id" -> startEditing._2, 
          "ref" -> elemRef, 
          "name" -> "gmd:descriptiveKeywords", 
          "href" -> "local://che.keyword.get?thesaurus=external.theme.inspire-theme&id=http://rdfdata.eionet.europa.eu/inspirethemes/themes/15&locales=fr,en,de,it")
     (request.execute().value.getXml)
    }.get
  }
  def keywordPresent = {
    val html = keywordHtml
    val germanInput = keywordHtml \\ "input" find (n => (n @@ "value" headOption) == Some("Gebäude"))
    
    germanInput must beSome
  }
  
}