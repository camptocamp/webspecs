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
    def performImport(lang: String) = {
      val baseImportRequest = ImportMetadata.defaultsWithReplacements(replacements, "/geocat/data/" + lang + "_Search_MD.iso19139.che.xml", false, classOf[ProcessImportedMetadataSpec])._2
      val importRequest = baseImportRequest.copy(uuidAction = UuidAction.None)
      lang -> importRequest().value.id
    }
    val idsAndLangCodes = List("FR", "DE", "EN", "XX") map (performImport)
    idsAndLangCodes foreach { case (_, id) => registerNewMd(Id(id)) }

    Map(idsAndLangCodes: _*)
  }

  lazy val idToLocalMap = importedMetadataId.map{case (key,value) => value -> key}
  
  def findCodesFromResults(xml:NodeSeq) = {
          val records = xml \\ "Record"

      val recordIds = records \ "info" \ "id" map (_.text.trim)
recordIds flatMap idToLocalMap.get

  }
  def find(xmlResponse: NodeSeq, expectedMetadata: String): Result = {
    val foundIdsMatchingImportedMd = findCodesFromResults(xmlResponse) 

    expectedMetadata match {
      case "all" => foundIdsMatchingImportedMd must haveTheSameElementsAs(importedMetadataId.keys)
      case "no" | "none" => foundIdsMatchingImportedMd must beEmpty
      case locales =>
        val localeSet = locales.split(" and ").toSet.map { (_: String).trim.toUpperCase }
        foundIdsMatchingImportedMd must haveTheSameElementsAs(localeSet)
    }

  }
}