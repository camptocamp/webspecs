package c2c.webspecs
package geonetwork
package spec.search

import org.specs2.specification.Step
import scala.xml.Node
import csw._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml.NodeSeq

@RunWith(classOf[JUnitRunner])
class BasicSearchOrderSpec extends GeonetworkSpecification with SearchSettingsSpecification { def is =
  "Title search order".title ^
  "Test the search order by title" ^ Step(setup) ^ Step(getSearchSetting) ^
      "Import several metadata with interesting titles and languages" ^ Step(importMd) ^
      "Set Search setting so that the request langauge is not sorted and all languages are allowed but the context language is considered" ^ Step(setSearchSetting(only=false, sorted = false, ignored = false)) ^
      "Sort by title in french and verify all MD are correctly sorted"      ! frTitleSearch ^
      "Sort by title in english and verify all MD are correctly sorted"     ! enTitleSearch ^
                                                                Step(resetSearchSetting)   ^ Step(tearDown)
                                                                        
  def pathToSearchMetadata = "/geonetwork/data/csw/search/"

  val timeStamp = System.currentTimeMillis()
  def importMd = {
    def doImport(lang:String,title:Node) = {
        val replacements = Map("{lang}" -> lang, "{title}" -> title.toString, "{uuid}" -> timeStamp.toString)
        val impRequest = ImportMetadata.defaultsWithReplacements(replacements, pathToSearchMetadata+"templated-name-lang.iso19139.xml", false, getClass, ImportStyleSheets.NONE)._2
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
    doImport("fre", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">b fra is fr</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("fre", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">A FRA EN and FR is FR</gmd:LocalisedCharacterString></gmd:textGroup>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#EN">A FRA EN and FR is EN</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
  }

  lazy val baselineSearch = {
    val response = XmlSearch( 10, "abstract" -> timeStamp).execute()
    response.value.records map (_.title)
  }
  def frTitleSearch = {
    implicit val fraresolver = new GeonetworkURIResolver(){
       override def locale = "fre"
    }
    val response = XmlSearch( 10,
      "abstract" -> timeStamp,
      "sortBy" -> "_title",
      'sortOrder -> 'reverse).execute()(context, fraresolver)
    val records = response.value.records
    
    val titles = records map (_.title)
    titles must contain("A ENG EN and FR is FR", "A FRA EN and FR is FR", "b eng en and fr is fr", "b fra is fr", "G eng is fr", "xx", "yy", "zz").only.inOrder
  }
  def enTitleSearch = {
     implicit val fraresolver = new GeonetworkURIResolver(){
      override def locale = "eng"
    }
    val response = XmlSearch( 10,
      "abstract" -> timeStamp,
      "sortBy" -> "_title",
      'sortOrder -> 'reverse).execute()(context, fraresolver)
    val records = response.value.records
    
    val titles = records map (_.title)
    val ti = records map (r => (r.infoValue("id"),r.recordValue("docLocale"), r.title))
    println(ti)
    titles must contain("A ENG EN and FR is EN", "A FRA EN and FR is EN", "b eng en and fr is en", "b fra is fr", "G eng is fr", "xx", "yy", "zz").only.inOrder
  }
}