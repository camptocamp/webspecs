package c2c.webspecs
package geonetwork
package geocat
package spec.WP9

import java.util.Enumeration
import collection.JavaConverters._
import csw._
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.ImportStyleSheets.NONE
import c2c.webspecs.geonetwork._
import c2c.webspecs.{ XmlValue, Response, IdValue, GetRequest }
import accumulating._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import scala.xml.transform.BasicTransformer
import c2c.webspecs.geonetwork.geocat.spec.WP3.ProcessImportedMetadataSpec
import scala.xml.Node
import scala.xml.XML
import scala.xml.Elem
import org.specs2.execute.Result
import java.util.Date
import org.specs2.matcher.ContainMatcher
import org.specs2.control.LazyParameters
import org.specs2.control.LazyParameter
import scala.xml.NodeSeq
import org.specs2.control.LazyParameter
import java.util.zip.ZipFile
import scalax.file.Path
import scalax.file.defaultfs.DefaultPath
import java.io.InputStreamReader
import scalax.io.Resource
import java.util.zip.ZipEntry
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream
import java.io.File
import java.io.FileOutputStream
import org.apache.http.entity.mime.content.ByteArrayBody
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
      true,
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