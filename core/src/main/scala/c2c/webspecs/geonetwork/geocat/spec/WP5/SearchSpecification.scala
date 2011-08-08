package c2c.webspecs
package geonetwork
package geocat
package spec.WP5
import c2c.webspecs.geonetwork.geocat.spec.WP3.ProcessImportedMetadataSpec
import java.util.Date
import org.specs2.matcher.Matcher
import scala.xml.NodeSeq
import org.specs2.execute.Result

private[WP5] abstract class SearchSpecification extends GeocatSpecification {
  val time = new Date().getTime().toString
  lazy val importedMetadataId = {
    val replacements = Map("{uuid}" -> uuid.toString, "{timestamp}" -> time.toString)
    def performImport(lang: String) = lang -> ImportMetadata.defaultsWithReplacements(replacements, "/geocat/data/" + lang + "_Search_MD.iso19139.che.xml", false, classOf[ProcessImportedMetadataSpec])._2().value.id
    val idsAndLangCodes = List("FR", "DE", "EN", "XX") map (performImport)
    idsAndLangCodes foreach { case (_, id) => registerNewMd(Id(id)) }

    Map(idsAndLangCodes: _*)
  }

  def find(xmlResponse: NodeSeq, expectedMetadata: String): Result = {
    val records = xmlResponse \\ "Record"

    val recordIds = records \ "info" \ "id" map (_.text.trim)
    val importedIds = importedMetadataId.values.toSet
    val foundIdsMatchingImportedMd = recordIds filter { id => importedIds contains id }

    expectedMetadata match {
      case "all" => foundIdsMatchingImportedMd must haveTheSameElementsAs(importedMetadataId.values)
      case "no" | "none" => foundIdsMatchingImportedMd must beEmpty
      case locales =>
        val localeSet = locales.split(" and ").toSet.map { (_: String).trim.toUpperCase }
        val ids = importedMetadataId.collect { case (key, id) if localeSet contains key => id }
        foundIdsMatchingImportedMd must haveTheSameElementsAs(ids)
    }

  }
}