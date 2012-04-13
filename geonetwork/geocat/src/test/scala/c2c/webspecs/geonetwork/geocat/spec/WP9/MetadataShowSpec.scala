package c2c.webspecs
package geonetwork
package geocat
package spec.WP9

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml._

@RunWith(classOf[JUnitRunner])
class MetadataShowSpec extends GeocatSpecification {
  def is =
    "This spec tests that metadata.show behaves as expected" ^ Step(setup) ^
      "Import the example metadata" ^ Step(importSampleFile) ^
      "metadata.show should not have xlinks tags" ! noXLinks ^
      "descriptiveKeywords should be correctly translated" ! translatedKeywords ^
      "titles should be correctly translated" ! translatedTitle ^
      "abstracts should be correctly translated" ! translatedAbstract ^
      Step(tearDown)

  lazy val importSampleFile = {
    val importMd = ImportMetadata.defaults(
      uuid,
      "/geocat/data/gm03_2_shows_xlinks_when_imported.xml",
      false,
      getClass,
      styleSheet = GeocatConstants.GM03_2_TO_CHE_STYLESHEET)._2
    val response = importMd.execute()

    val enResponse = ShowMetadata(view = MetadataViews.complete).execute(Id(response.value.id))(context, ShowMetadataResolver("eng"))
    val frResponse = ShowMetadata(view = MetadataViews.complete).execute(Id(response.value.id))(context, ShowMetadataResolver("fra"))
    new {
      val en = enResponse.value.getHtml;
      val fr = frResponse.value.getHtml
    }
  }

  def noXLinks = {
    val xlinkParents = importSampleFile.en \\ "_" filter { n => n.child.exists(n => n.isInstanceOf[Text] && (n.text.trim startsWith "xlink:")) }
    xlinkParents must beEmpty
  }

  def findText(metadata: NodeSeq, titleTranslation: String, partOfExpectedValue: String) = {
      val allRows = metadata \\ "tr"

      val rows = allRows filter (_.descendant \\ "tr" isEmpty)
      val correctRow = rows filter (n => n \\ "th" flatMap {_.descendant} exists {
        case Text(text) if text.trim.toLowerCase contains titleTranslation.toLowerCase() => true
        case _ => false
      })
      correctRow flatMap (_.descendant) collect {
        case Text(text) if text.trim.toLowerCase contains partOfExpectedValue.toLowerCase() => text.trim.toLowerCase().replaceAll("""\s+"""," ")
      }
  }
  
  def translatedKeywords = {
    val enKeyword = findText(importSampleFile.en, "descriptive", "cartography")
    val frKeyword = findText(importSampleFile.fr, "descriptifs", "cartographie")

    (enKeyword must haveTheSameElementsAs(Seq("cartography."))) and
      (frKeyword must haveTheSameElementsAs(Seq("cartographie.")))
  }
  def translatedTitle = {
    val en = findText(importSampleFile.en, "Title", "National Map")
    val fr = findText(importSampleFile.fr, "Titre", "Carte nationale")

    (en must haveTheSameElementsAs(Seq("national map 1:500'000"))) and
      (fr must haveTheSameElementsAs(Seq("carte nationale 1:500'000")))
  }
  def translatedAbstract = {
    val en = findText(importSampleFile.en, "abstract", "National Map")
    val fr = findText(importSampleFile.fr, "Résumé", "carte nationale")

    (en.head must contain("The National Map 1:500'000 is a topographic map giving an overview of Switzerland".toLowerCase)) and
      (fr.head must contain("La carte nationale au 1:500'000".toLowerCase))
  }

  case class ShowMetadataResolver(lang: String) extends UriResolver {
    def apply(service: String, params: Seq[(String, String)]): String = {
      "http://" + Properties.testServer + "/geonetwork/srv/" + lang + "/" + service + paramsToString(params, "?")
    }
  }
}