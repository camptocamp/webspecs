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

  def translatedKeywords = {
    def keyword(metadata: NodeSeq, keywordTranslation: String, word: String) = {
      val allRows = metadata \\ "tr"

      val rows = allRows filter (_.descendant \\ "tr" isEmpty)
      rows flatMap (_.descendant) collect {
        case Text(text) if text.trim.toLowerCase contains word => text.trim.toLowerCase()
      }
    }
    val enKeyword = keyword(importSampleFile.en, "descriptive", "cartography")
    val frKeyword = keyword(importSampleFile.fr, "descriptifs", "cartographie")

    (enKeyword must haveTheSameElementsAs(Seq("cartography."))) and
      (frKeyword must haveTheSameElementsAs(Seq("cartographie.")))
  }
  def translatedTitle = {
    def keyword(metadata: NodeSeq, keywordTranslation: String, word: String) = {
      val allRows = metadata \\ "tr"

      val rows = allRows filter (_.descendant \\ "tr" isEmpty)
      rows flatMap (_.descendant) collect {
        case Text(text) if text.trim.toLowerCase contains word => text.trim.toLowerCase()
      }
    }
    val enKeyword = keyword(importSampleFile.en, "Title", "National Map")
    val frKeyword = keyword(importSampleFile.fr, "Titre", "Carte nationale")

    (enKeyword must haveTheSameElementsAs(Seq("National Map 1:500'000"))) and
      (frKeyword must haveTheSameElementsAs(Seq("Carte nationale 1:500'000")))
  }
  def translatedAbstract = {
    def keyword(metadata: NodeSeq, keywordTranslation: String, word: String) = {
      val allRows = metadata \\ "tr"

      val rows = allRows filter (_.descendant \\ "tr" isEmpty)
      rows flatMap (_.descendant) collect {
        case Text(text) if text.trim.toLowerCase contains word => text.trim.toLowerCase()
      }
    }
    val enKeyword = keyword(importSampleFile.en, "descriptive", "cartography")
    val frKeyword = keyword(importSampleFile.fr, "Résumé", "carte nationale")

    (enKeyword must contain("The National Map 1:500'000 is a topographic map giving an overview of Switzerland")) and
      (frKeyword must contain("La carte nationale au 1:500'000"))
  }

  case class ShowMetadataResolver(lang: String) extends UriResolver {
    def apply(service: String, params: Seq[(String, String)]): String = {
      "http://" + Properties.testServer + "/geonetwork/srv/" + lang + "/" + service + paramsToString(params, "?")
    }
  }
}