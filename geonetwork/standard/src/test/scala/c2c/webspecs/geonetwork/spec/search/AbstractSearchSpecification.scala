package c2c.webspecs
package geonetwork
package spec.search

import java.util.Date
import scala.xml.NodeSeq
import org.specs2.execute.Result

trait AbstractSearchSpecification[ResultType] {
  self: GeonetworkSpecification =>
  val time = new Date().getTime().toString

  def pathToSearchMetadata = "/geonetwork/data/csw/search/"
  lazy val importedMetadataId = {
    val replacements = Map("{uuid}" -> uuid.toString, "{timestamp}" -> time.toString)
    def performImport(lang: String) = {
      val baseImportRequest = ImportMetadata.defaultsWithReplacements(replacements, pathToSearchMetadata + lang + "_Search_MD.iso19139.xml", false, classOf[SearchSpecification])._2
      val importRequest = baseImportRequest.copy(uuidAction = UuidAction.None)
      lang -> importRequest.execute().value.id
    }
    val idsAndLangCodes = List("FR", "DE", "EN", "XX") map (performImport)
    idsAndLangCodes foreach { case (_, id) => registerNewMd(Id(id)) }

    Map(idsAndLangCodes: _*)
  }

  lazy val idToLocalMap = importedMetadataId.map { case (key, value) => value -> key }

  def importExtraMd(numberOfRecords: Int, md: String = "/geonetwork/data/valid-metadata.iso19139.xml", identifier: String, styleSheet: ImportStyleSheets.ImportStyleSheet = ImportStyleSheets.NONE) =
    importMd(numberOfRecords, md, identifier, styleSheet)

  def findCodesFromResults(records: ResultType): Seq[String]

  def find(records: ResultType, expectedMetadata: String, maxRecords: Int = -1): Result = {
    val foundIdsMatchingImportedMd = findCodesFromResults(records) 

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