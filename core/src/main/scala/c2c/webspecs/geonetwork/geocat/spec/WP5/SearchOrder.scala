package c2c.webspecs
package geonetwork
package geocat
package spec.WP5

import c2c.webspecs.geonetwork.geocat.GeocatSpecification
import org.specs2.specification.Step
import scala.xml.Node
import csw._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SearchOrder extends GeocatSpecification { def is =
  "Title search order".title ^
  "Test the search order by title" ^ Step(setup) ^
      "Import several metadata with interesting titles and languages" ^ Step(importMd) ^
      "Do a normal search to get the baseline search"                 ^ Step(baselineSearch) ^
      "Now sort by title in french and verify all MD are correctly sorted"      ! frTitleSearch ^
      "Now sort by title in english and verify all MD are correctly sorted"      ! enTitleSearch ^
                                                                        Step(tearDown)

  val timeStamp = System.currentTimeMillis()
  def importMd = {
    def doImport(lang:String,title:Node) = {
        val replacements = Map("{lang}" -> lang, "{title}" -> title.toString, "{uuid}" -> timeStamp.toString)
        val impRequest = ImportMetadata.defaultsWithReplacements(replacements, "/geocat/data/templated-name-lang.iso19139.che.xml", false, classOf[SearchOrder], ImportStyleSheets.NONE)._2
        registerNewMd(impRequest().value)
    }

    doImport("eng", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#DE">zz</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("fra", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">A FR EN caps FR</gmd:LocalisedCharacterString></gmd:textGroup>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#EN">A FR EN caps EN</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("eng", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">A EN FR caps FR</gmd:LocalisedCharacterString></gmd:textGroup>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#EN">A EN FR caps EN</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("eng", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">b en fr lower fr</gmd:LocalisedCharacterString></gmd:textGroup>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#EN">b en fr lower en</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("fra", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">b fr lower</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("eng", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">G en lower</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
  }

  lazy val baselineSearch = {
    val cswResponse = CswGetRecordsRequest(PropertyIsEqualTo("abstract", timeStamp.toString).xml, ResultTypes.results, elementSetName = ElementSetNames.summary, outputSchema=OutputSchemas.Record)()
    cswResponse.value.getXml \\ "SummaryRecord" \\ "title"
  }
  def frTitleSearch = {
    val cswResponse = CswGetRecordsRequest(
            PropertyIsEqualTo("abstract", timeStamp.toString).xml, 
            ResultTypes.results, 
            outputSchema=OutputSchemas.Record,
            elementSetName = ElementSetNames.summary,
            url = "fra/csw",
            sortBy = List(SortBy("title", false)))()
    val records = cswResponse.value.getXml \\ "SummaryRecord" \\ "title" map (_.text)

    records must haveTheSameElementsAs(List("A FR EN caps", "b fr lower", "A EN FR caps", "b en fr lower", "G en lower","zz"))
  }
  def enTitleSearch = {
    val cswResponse = CswGetRecordsRequest(
            PropertyIsEqualTo("abstract", timeStamp.toString).xml, 
            ResultTypes.results, 
            elementSetName = ElementSetNames.summary,
            url = "eng/csw",
            outputSchema=OutputSchemas.Record,
            sortBy = List(SortBy("title", false)))()
    val records = cswResponse.value.getXml \\ "SummaryRecord" \\ "title" map (_.text)

    records must haveTheSameElementsAs(List("A EN FR caps", "b en fr lower", "G en lower", "zz", "A FR EN caps", "a fr lower"))
  }
}