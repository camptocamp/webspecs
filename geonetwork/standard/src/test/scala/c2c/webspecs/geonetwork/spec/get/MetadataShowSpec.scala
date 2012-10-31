package c2c.webspecs
package geonetwork
package spec.get

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml._
import org.specs2.specification.Fragments

@RunWith(classOf[JUnitRunner])
class MetadataShowSpec extends GeonetworkSpecification {
  def is =
    "This spec tests that metadata.show behaves as expected" ^ Step(setup) ^
      "Import the example metadata" ^ Step(importSampleFile) ^
      "descriptiveKeywords should be correctly translated" ! translatedKeywords ^
      "titles should be correctly translated" ! translatedTitle ^
      "abstracts should be correctly translated" ! translatedAbstract ^
      extraTests ^
      Step(tearDown)

  def extraTests: Fragments = success
  def importStyleSheet:ImportStyleSheets.ImportStyleSheet = ImportStyleSheets.NONE
  def xmlFile = "/geonetwork/data/metadata-show-spec.xml"
    
  lazy val importSampleFile = {
    val importMd = ImportMetadata.defaults(
      uuid,
      xmlFile,
      false,
      getClass,
      importStyleSheet)._2
    val response = importMd.execute()

    val enResponse = ShowMetadata(view = MetadataViews.complete).execute(Id(response.value.id))(context, ShowMetadataResolver("eng"))
    val frResponse = ShowMetadata(view = MetadataViews.complete).execute(Id(response.value.id))(context, ShowMetadataResolver("fre"))
    new {
      val en = enResponse.value.getHtml;
      val fr = frResponse.value.getHtml
    }
  }


  def translatedKeywords = {
    val enText = importSampleFile.en.toString
    val frText = importSampleFile.fr.toString

     (enText.contains("ENKEYWORD11223344") must beTrue) and
         (frText.contains("ENKEYWORD11223344") must beFalse) and
         (frText.contains("FRKEYWORD22334411") must beTrue) and
         (enText.contains("FRKEYWORD22334411") must beFalse)
  }
  def translatedTitle = {
    val enText = importSampleFile.en.toString
    val frText = importSampleFile.fr.toString

     (enText.contains("ENTitle11223344") must beTrue) and
         (frText.contains("ENTitle11223344") must beFalse) and
         (frText.contains("FRTitle22334411") must beTrue) and
         (enText.contains("FRTitle22334411") must beFalse)
  }
  def translatedAbstract = {
    val enText = importSampleFile.en.toString
    val frText = importSampleFile.fr.toString
     (enText.contains("ENAbstract11223344") aka """en contains "ENAbstract11223344" """ must beTrue) and
         (frText.contains("ENAbstract11223344") aka """fr not contains "ENAbstract11223344" """must beFalse) and
         (frText.contains("FRAbstract22334411") aka """fr contains "FRAbstract22334411" """ must beTrue) and
         (enText.contains("FRAbstract22334411") aka """en not contains "FRAbstract22334411" """ must beFalse)

  }

  case class ShowMetadataResolver(lang: String) extends UriResolver {
    def apply(service: String, params: Seq[(String, String)]): String = {
      "http://" + Properties.testServer + "/geonetwork/srv/" + lang + "/" + service + paramsToString(params, "?")
    }
  }
}