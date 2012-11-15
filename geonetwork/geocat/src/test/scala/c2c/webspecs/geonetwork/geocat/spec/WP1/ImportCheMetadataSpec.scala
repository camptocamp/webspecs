package c2c.webspecs
package geonetwork
package geocat
package spec.WP1

import csw._
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ImportCheMetadataSpec  extends GeocatSpecification {  def is = 
	
  "This specification tests using the iso19139.CHE schema"                  ^ Step(setup)               ^
    "Inserting a CHE metadata"                                              ^ Step(importAndGetMetadata) ^
    "Should suceed with a 200 response"                                     ! {importAndGetMetadata must haveA200ResponseCode}   ^
    "And the new metadata should be accessible via xml.metadata.get"        ! correctMetadataWasRetrieved      ^
    "As well as via csw getRecordById"                                      ! cswGetInsertedMd      ^
    "As well as via csw getRecords"                                         ! cswGetInsertedMdByCswGetRecords      ^
                                                                              Step(tearDown)

  lazy val importAndGetMetadata = {
    val mdId = importMd(1, "/geocat/data/metadata.iso19139.che.xml", uuid.toString).head
    GetRawMetadataXml.execute(mdId)
  }
  lazy val metadataXml = importAndGetMetadata.value.getXml

  def correctMetadataWasRetrieved = {
    val charString = (metadataXml \\ "citation" \ "CI_Citation" \ "title" \ "CharacterString")
    val frLocalizedString = (metadataXml \\ "citation" \ "CI_Citation" \ "title" \\ "LocalisedCharacterString" find (n => (n \\ "@locale").text == "#FR")).map(_.text)
    val abstractText = (metadataXml \\ "abstract" \\ "CharacterString")
    val abstractLocalisedFRtext = (metadataXml \\ "abstract" \\ "LocalisedCharacterString" find (n => (n \\ "@locale").text == "#FR")).map(_.text)
    val abstractLocalisedENtext = (metadataXml \\ "abstract" \\ "LocalisedCharacterString" find (n => (n \\ "@locale").text == "#EN")).map(_.text)
    val abstractLocalisedDEtext = (metadataXml \\ "abstract" \\ "LocalisedCharacterString" find (n => (n \\ "@locale").text == "#DE")).map(_.text)

    // Test che: fields
    val cheLocalisedUrl = (metadataXml \\ "CHE_MD_Metadata" \ "contact" \ "CHE_CI_ResponsibleParty" \ "contactInfo" \ "CI_Contact" \
      "onlineResource" \ "CI_OnlineResource" \ "linkage" \ "PT_FreeURL" \ "URLGroup" \ "LocalisedURL").text

    (charString must beEmpty) and
      (frLocalizedString must beSome("FR Title")) and
      (cheLocalisedUrl must_== "http://www.awnl.llv.li") and
      (abstractText must beEmpty) and
      (abstractLocalisedFRtext must beSome("FR abstract "+uuid)) and
      (abstractLocalisedENtext must beSome("EN  abstract "+uuid)) and
      (abstractLocalisedDEtext must beSome("DE  abstract "+uuid))
  }

  def cswGetInsertedMd = {
    val fileId = (metadataXml \\ "fileIdentifier" text).trim()
    val response = CswGetRecordById(fileId, geocat.OutputSchemas.CheIsoRecord).execute()
    (response must haveA200ResponseCode) and
      (response.value.getXml \\ "CHE_MD_Metadata" must not beEmpty)
  }

  def cswGetInsertedMdByCswGetRecords = {
    val fileId = (metadataXml \\ "fileIdentifier" text).trim()
    val filter = PropertyIsEqualTo("Identifier", fileId).xml
    val response = CswGetRecordsRequest(filter, outputSchema = geocat.OutputSchemas.CheIsoRecord, resultType = ResultTypes.results).execute()
    val xml = response.value.getXml
    (response must haveA200ResponseCode) and
      ((xml \\ "SearchResults" \@ "numberOfRecordsReturned").head.trim.toInt must be_>=(1)) and
      (xml \\ "CHE_MD_Metadata" must not beEmpty)
  }

}