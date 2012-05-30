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
class SearchOrderSpec extends GeocatSpecification { def is =
  "Title search order".title ^
  "Test the search order by title" ^ Step(setup) ^
      "Import several metadata with interesting titles and languages" ^ Step(importMd) ^
      "Sort by title in french and verify all MD are correctly sorted"      ! frTitleSearch ^
      "Sort by title in english and verify all MD are correctly sorted"     ! enTitleSearch ^
                                                                        Step(tearDown)

  val timeStamp = System.currentTimeMillis()
  def importMd = {
    def doImport(lang:String,title:Node) = {
        val replacements = Map("{lang}" -> lang, "{title}" -> title.toString, "{uuid}" -> timeStamp.toString)
        val impRequest = ImportMetadata.defaultsWithReplacements(replacements, "/geocat/data/templated-name-lang.iso19139.che.xml", false, classOf[SearchOrderSpec], ImportStyleSheets.NONE)._2
        registerNewMd(impRequest.execute().value)
    }

    doImport("eng", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#DE">zz</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("ita", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#DE">yy</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("ita", <gmd:PT_FreeText>
                      <gmd:textGroup><gmd:LocalisedCharacterString locale="#DE">xx</gmd:LocalisedCharacterString></gmd:textGroup>
                    </gmd:PT_FreeText>)
    doImport("eng", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">A ENG EN and FR is FR</gmd:LocalisedCharacterString></gmd:textGroup>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#EN">A ENG EN and FR is EN</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("eng", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">G eng is fr</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("eng", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">b eng en and fr is fr</gmd:LocalisedCharacterString></gmd:textGroup>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#EN">b eng en and fr is en</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("fra", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">b fra is fr</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("fra", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">A FRA EN and FR is FR</gmd:LocalisedCharacterString></gmd:textGroup>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#EN">A FRA EN and FR is EN</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
  }

  lazy val baselineSearch = {
    val cswResponse = CswGetRecordsRequest(PropertyIsEqualTo("abstract", timeStamp.toString).xml, ResultTypes.results, elementSetName = ElementSetNames.summary, outputSchema = OutputSchemas.Record).execute()
    cswResponse.value.getXml \\ "SummaryRecord" \\ "title"
  }
  def frTitleSearch = {
    val cswResponse = CswGetRecordsRequest(
      PropertyIsEqualTo("abstract", timeStamp.toString).xml,
      ResultTypes.results,
      outputSchema = OutputSchemas.Record,
      elementSetName = ElementSetNames.summary,
      url = "fra/csw",
      sortBy = List(SortBy("_defaultTitle", true))).execute()
    val records = cswResponse.value.getXml \\ "SummaryRecord" \\ "title" map (_.text)

    records must contain("A ENG EN and FR is FR", "A FRA EN and FR is FR", "b eng en and fr is fr", "b fra is fr", "G eng is fr", "xx", "yy", "zz").only.inOrder
  }
  def enTitleSearch = {
    val cswResponse = CswGetRecordsRequest(
      PropertyIsEqualTo("abstract", timeStamp.toString).xml,
      ResultTypes.results,
      elementSetName = ElementSetNames.summary,
      url = "eng/csw",
      outputSchema = OutputSchemas.Record,
      sortBy = List(SortBy("_defaultTitle", true))).execute()
    val records = cswResponse.value.getXml \\ "SummaryRecord" \\ "title" map (_.text)

    records must contain("A ENG EN and FR is EN", "A FRA EN and FR is EN", "b eng en and fr is en", "b fra is fr", "G eng is fr", "xx", "yy", "zz").only.inOrder
  }
}