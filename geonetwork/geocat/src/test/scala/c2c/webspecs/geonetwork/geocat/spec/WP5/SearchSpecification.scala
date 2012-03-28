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
      lang -> importRequest.execute().value.id
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
  
  def find(xmlResponse: NodeSeq, expectedMetadata: String, maxRecords: Int = -1): Result = {
    val foundIdsMatchingImportedMd = findCodesFromResults(xmlResponse) 

    (expectedMetadata, maxRecords) match {
      case ("all",-1)  => foundIdsMatchingImportedMd must haveTheSameElementsAs(importedMetadataId.keys)
      case ("all",maxRecords)  => (importedMetadataId.keys must containAllOf(foundIdsMatchingImportedMd)) and (foundIdsMatchingImportedMd must haveSize(maxRecords))
      case ("no" | "none", _) => foundIdsMatchingImportedMd must beEmpty
      case (locales, -1) =>
        val localeSet = locales.split(" and ").toSet.map { (_: String).trim.toUpperCase }
        foundIdsMatchingImportedMd must haveTheSameElementsAs(localeSet)
      case (locales, maxRecords) =>
        val localeSet = locales.split(" and ").toSet.map { (_: String).trim.toUpperCase }.toList
        (foundIdsMatchingImportedMd must containAllOf(localeSet)) and (foundIdsMatchingImportedMd must haveSize(maxRecords))
    }

  }
}