package c2c.webspecs
package geonetwork
package spec.search
import c2c.webspecs.geonetwork.GeonetworkSpecification

import org.specs2.specification.Step
import scala.xml.Node
import csw._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml.NodeSeq

trait AbstractSearchOrderSpecSpecification {
  self: GeonetworkSpecification =>

 def titleExtension:String 
 def is =
  ("TitleSearchOrder"+titleExtension).title ^ sequential ^
  "Test the search order by title" ^ Step(setup) ^ 
      "Import several metadata with interesting titles and languages" ^ Step(importMd) ^ endp ^
      "Set Search setting so that the request language is not sorted, all languages are allowed and the metadata in the context language is considered more important" ^ Step(setSearchSetting(only="prefer_locale", sorted = false, ignored = false)) ^
      "Sort by title in french and verify all MD are correctly sorted"      ! frTitleSearch ^
      "Sort by title in english and verify all MD are correctly sorted"     ! enTitleSearch ^ endp ^
      "Set Search setting so that the request language is not sorted and only documents of the the context language is allowed" ^ Step(setSearchSetting(only="only_docLocale", sorted = false, ignored = false)) ^
      "Sort by title in french and verify all MD are correctly sorted"      ! frTitleDocLocaleSearchOnly ^
      "Sort by title in english and verify all MD are correctly sorted"     ! enTitleDocLocaleSearchOnly ^ endp ^
      "Set Search setting so that the request language is not sorted and only documents with abstract in the context language is allowed" ^ Step(setSearchSetting(only="only_locale", sorted = false, ignored = false)) ^
      "Sort by title in french and verify all MD are correctly sorted"      ! frTitleLocaleSearchOnly ^
      "Sort by title in english and verify all MD are correctly sorted"     ! enTitleLocaleSearchOnly ^ endp ^
      "Set Search setting so that the request language is sorted, all languages are allowed and the metadata in the context language is considered more important" ^ Step(setSearchSetting(only="prefer_locale", sorted = true, ignored = false)) ^
      "Sort by title in french and verify all MD are correctly sorted"      ! frTitleSearchSorted ^
      "Sort by title in english and verify all MD are correctly sorted"     ! enTitleSearchSorted ^ endp ^
      "Set Search setting so that the request language is not sorted and the context language is ignored" ^ Step(setSearchSetting(only="prefer_locale", sorted = false, ignored = true)) ^
      "Sort by title in french and verify all MD are correctly sorted"      ! frTitleSearchIgnored ^
      "Sort by title in english and verify all MD are correctly sorted"     ! enTitleSearchIgnored ^ endp ^
                                                                Step(tearDown)

  def pathToSearchMetadata = "/geonetwork/data/csw/search/"
  def doSearch(lang:String): Seq[String]
  
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
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">E2 ENG EN and FR is FR</gmd:LocalisedCharacterString></gmd:textGroup>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#EN">Z2 ENG EN and FR is EN</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("eng", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">G eng is fr</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("eng", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">e eng en and fr is fr</gmd:LocalisedCharacterString></gmd:textGroup>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#EN">e eng en and fr is en</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("fre", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">é fra is fr</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("fre", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">A FRA EN and FR is FR</gmd:LocalisedCharacterString></gmd:textGroup>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#EN">A FRA EN and FR is EN</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
    doImport("fre", <gmd:PT_FreeText>
                     <gmd:textGroup><gmd:LocalisedCharacterString locale="#FR">Z3 FRA EN and FR is FR</gmd:LocalisedCharacterString></gmd:textGroup>
                      <gmd:textGroup><gmd:LocalisedCharacterString locale="#EN">E3 FRA EN and FR is EN</gmd:LocalisedCharacterString></gmd:textGroup>
                   </gmd:PT_FreeText>)
  }

  def frTitleSearch = {
    val titles = doSearch("fre")
    titles must contain("A ENG EN and FR is FR", "A FRA EN and FR is FR", "E2 ENG EN and FR is FR", "e eng en and fr is fr", "é fra is fr", "G eng is fr", "xx", "yy", "Z3 FRA EN and FR is FR", "zz").only.inOrder
  }
  def enTitleSearch = {
    val titles = doSearch("eng")
    titles must contain("A ENG EN and FR is EN", "A FRA EN and FR is EN", "E3 FRA EN and FR is EN", "e eng en and fr is en", "é fra is fr", "G eng is fr", "xx", "yy", "Z2 ENG EN and FR is EN", "zz").only.inOrder
  }
  def frTitleDocLocaleSearchOnly = {
    val records = doSearch("fre")
    records must contain("A FRA EN and FR is FR", "é fra is fr", "Z3 FRA EN and FR is FR").only.inOrder
  }
  def enTitleDocLocaleSearchOnly = {
    val records = doSearch("eng")
    records must contain("A ENG EN and FR is EN", "e eng en and fr is en", "G eng is fr", "Z2 ENG EN and FR is EN", "zz").only.inOrder
  }
  def frTitleLocaleSearchOnly = {
    val records = doSearch("fre")
    records must contain("A FRA EN and FR is FR", "é fra is fr", "Z3 FRA EN and FR is FR").only.inOrder
  }
  def enTitleLocaleSearchOnly = {
    val records = doSearch("eng")
    // Note: all docs have abstract in english so searching by locale finds all documents
    records must contain("A ENG EN and FR is EN", "A FRA EN and FR is EN", "E3 FRA EN and FR is EN", "e eng en and fr is en", "é fra is fr", "G eng is fr", "xx", "yy", "Z2 ENG EN and FR is EN", "zz").only.inOrder
  }
  def frTitleSearchSorted = {
    val records = doSearch("fre")
    (records must contain("A ENG EN and FR is FR", "A FRA EN and FR is FR", "e eng en and fr is fr", "é fra is fr", "E2 ENG EN and FR is FR", "G eng is fr", "xx", "yy", "Z3 FRA EN and FR is FR", "zz").only) and
    (records.take(3) must contain("A FRA EN and FR is FR", "é fra is fr", "Z3 FRA EN and FR is FR").only.inOrder) and
    (records.drop(3) must contain("A ENG EN and FR is FR", "e eng en and fr is fr", "G eng is fr", "zz" ).inOrder) and 
    (records.drop(3) must contain("xx", "yy").inOrder)
  }
  def enTitleSearchSorted = {
    val records = doSearch("eng")
    (records must contain("A ENG EN and FR is EN", "A FRA EN and FR is EN", "E3 FRA EN and FR is EN", "e eng en and fr is en", "é fra is fr", "G eng is fr", "xx", "yy", "Z2 ENG EN and FR is EN", "zz").only) and
    (records.take(5) must contain("A ENG EN and FR is EN", "e eng en and fr is en", "G eng is fr", "Z2 ENG EN and FR is EN", "zz").only.inOrder) and 
    (records.drop(5) must contain("xx", "yy").inOrder) and
    (records.drop(5) must contain("A FRA EN and FR is EN", "E3 FRA EN and FR is EN", "é fra is fr").inOrder)
  }
  def frTitleSearchIgnored = {
    val records = doSearch("fre")
    records must contain("A ENG EN and FR is FR", "A FRA EN and FR is FR", "E2 ENG EN and FR is FR", "e eng en and fr is fr", "é fra is fr", "G eng is fr", "xx", "yy", "Z3 FRA EN and FR is FR", "zz").only.inOrder
  }
  def enTitleSearchIgnored = {
    val records = doSearch("eng")
    records must contain("A ENG EN and FR is EN", "A FRA EN and FR is EN", "E3 FRA EN and FR is EN", "e eng en and fr is en", "é fra is fr", "G eng is fr", "xx", "yy", "Z2 ENG EN and FR is EN", "zz").only.inOrder
  }

}