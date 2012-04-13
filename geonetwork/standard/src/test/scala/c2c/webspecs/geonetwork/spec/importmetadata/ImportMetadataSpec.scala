package c2c.webspecs
package geonetwork
package spec.importmetadata

import csw._
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

/**
 * Import metadata and verify that it was correctly imported
 */
@RunWith(classOf[JUnitRunner])
class ImportMetadataSpec extends GeonetworkSpecification {  def is =

  "This specification tests in the iso19139 schema"                         ^ Step(setup)               ^
    "Inserting a iso19139 metadata"                                         ^ Step(importAndGetMetadata)      ^
    "Should succeed with a 200 response"                                    ! {importAndGetMetadata must haveA200ResponseCode}   ^
    "And the new metadata should be accessible via xml.metadata.get"        ! correctMetadataWasRetrieved      ^
    "As well as via csw getRecordById"                                      ! cswGetInsertedMd      ^
    "As well as via csw getRecords"                                         ! cswGetInsertedMdByCswGetRecords      ^
                                                                              Step(tearDown)

  lazy val importAndGetMetadata = {
    val mdId = importMd(1, "/geonetwork/data/multilingual-metadata.iso19139.xml", uuid.toString).head
    GetRawMetadataXml.execute(mdId)
  }
  lazy val metadataXml = importAndGetMetadata.value.getXml

  def correctMetadataWasRetrieved = {
    val charString = (metadataXml \\ "citation" \ "CI_Citation" \ "title" \ "CharacterString").text.trim
    val frLocalizedString = (metadataXml \\ "citation" \ "CI_Citation" \ "title" \\ "LocalisedCharacterString" find (n => (n \\ "@locale").text == "#FR")).map(_.text)

    val abstractText = (metadataXml \\ "abstract" \\ "CharacterString").text.trim
    val abstractLocalisedFRtext = (metadataXml \\ "abstract" \\ "LocalisedCharacterString" find (n => (n \\ "@locale").text == "#FR")).map(_.text)
    val abstractLocalisedDEtext = (metadataXml \\ "abstract" \\ "LocalisedCharacterString" find (n => (n \\ "@locale").text == "#GE")).map(_.text)

    (charString must_== "EN Title") and
      (frLocalizedString must beSome("FR Title")) and
      (abstractText must_== "EN Abstract "+uuid) and
      (abstractLocalisedFRtext must beSome("FR Abstract "+uuid)) and
      (abstractLocalisedDEtext must beSome("DE Abstract "+uuid))
  }

  def cswGetInsertedMd = {
    val fileId = (metadataXml \\ "fileIdentifier" text).trim()
    val md = CswGetRecordById(fileId, OutputSchemas.IsoRecord).execute()

    (md must haveA200ResponseCode) and
      ((md.value.getXml \\ "GetRecordByIdResponse" \ "MD_Metadata") must not beEmpty)
  }

  def cswGetInsertedMdByCswGetRecords = {
    val fileId = (metadataXml \\ "fileIdentifier").text.trim()
    val filter = PropertyIsEqualTo("Identifier", fileId).xml
    val md = CswGetRecordsRequest(filter, outputSchema = OutputSchemas.IsoRecord, resultType = ResultTypes.results).execute()
    val xml = md.value.getXml
    (md must haveA200ResponseCode) and
      ((xml \\ "SearchResults" \@ "numberOfRecordsReturned").head.trim must_==("1")) and
      (xml \\ "MD_Metadata" must haveSize(1))
  }
}