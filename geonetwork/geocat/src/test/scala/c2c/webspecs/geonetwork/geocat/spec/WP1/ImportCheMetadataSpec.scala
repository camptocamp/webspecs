package c2c.webspecs
package geonetwork
package geocat
package spec.WP1

import csw._
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.ImportStyleSheets.NONE
import c2c.webspecs.geonetwork._
import c2c.webspecs.{XmlValue, Response, IdValue, GetRequest}
import accumulating._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml.NodeSeq

@RunWith(classOf[JUnitRunner]) 
class ImportCheMetadataSpec  extends GeocatSpecification {  def is =

  "This specification tests using the iso19139.CHE schema"                  ^ Step(setup)               ^
    "Inserting a CHE metadata"                                              ^ importISO19139CCHE.toGiven   ^
    "Should suceed with a 200 response"                                     ^ import200Response         ^
    "And the new metadata should be accessible via xml.metadata.get"        ^ getInsertedMd.toThen      ^
    "As well as via csw getRecordById"                                      ^ cswGetInsertedMd.toThen      ^
    "As well as via csw getRecords"                                         ^ cswGetInsertedMdByCswGetRecords.toThen      ^
    "Deleting metadata"                                                      ^ deleteMetadata.toThen ^ Step(tearDown)

  type ImportResponseType = AccumulatedResponse1[IdValue, XmlValue]

  val importISO19139CCHE = (_:String) => {
    val (_,importMd) = ImportMetadata.defaults(uuid, "/geocat/data/metadata.iso19139.che.xml",true, getClass)
    
    val response = (importMd startTrackingThen GetRawMetadataXml).execute()

    response:ImportResponseType
    
  }

  val import200Response = a200ResponseThen.narrow[ImportResponseType]

  val getInsertedMd = (response:ImportResponseType) => {
    val xmlResponse = response.last

    xmlResponse.value.withXml{md =>

      println(md)
      val charString = (md \\ "citation"  \ "CI_Citation" \ "title" \ "CharacterString")
      val frLocalizedString = (md \\ "citation"  \ "CI_Citation" \ "title" \\ "LocalisedCharacterString" find (n =>  (n \\ "@locale").text == "#FR")).map(_.text)

      val abstractText = (md \\ "abstract" \\ "CharacterString")
      val abstractLocalisedFRtext = (md \\ "abstract" \\ "LocalisedCharacterString" find (n =>  (n \\ "@locale").text == "#FR")).map(_.text)
      val abstractLocalisedENtext = (md \\ "abstract" \\ "LocalisedCharacterString" find (n =>  (n \\ "@locale").text == "#EN")).map(_.text)
      val abstractLocalisedDEtext = (md \\ "abstract" \\ "LocalisedCharacterString" find (n =>  (n \\ "@locale").text == "#DE")).map(_.text)

      val ciContact = md \\ "CHE_MD_Metadata" \ "contact" \ "CHE_CI_ResponsibleParty" \ "contactInfo" \ "CI_Contact"
      val online = md \\ "CHE_MD_Metadata" \ "contact" \ "CHE_CI_ResponsibleParty" \ "contactInfo" \ "CI_Contact" \ "onlineResource" \ "CI_OnlineResource" 
      val group = md \\ "CHE_MD_Metadata" \ "contact" \ "CHE_CI_ResponsibleParty" \ "contactInfo" \ "CI_Contact" \ "onlineResource" \ "CI_OnlineResource" \ "linkage" \ "PT_FreeURL" \ "URLGroup"
      // Test che: fields
      val cheLocalisedUrl = (md \\ "CHE_MD_Metadata" \ "contact" \ "CHE_CI_ResponsibleParty" \ "contactInfo" \ "CI_Contact" \
        "onlineResource" \ "CI_OnlineResource" \ "linkage" \ "PT_FreeURL" \ "URLGroup" \ "LocalisedURL").text

//        ((node must_== "COmprehenisve Test") and (abstractText must_== "xx"))
        (charString must beEmpty) and
          (frLocalizedString must beSome("FR Title")) and
          (cheLocalisedUrl must_== "http://www.awnl.llv.li") and
          (abstractText must beEmpty) and 
          (abstractLocalisedFRtext must beSome("FR abstract")) and
          (abstractLocalisedENtext must beSome("EN  abstract")) and
          (abstractLocalisedDEtext must beSome("DE  abstract"))
    }
  }

  val cswGetInsertedMd = (response:ImportResponseType) => {
    val fileId = response.last.value.withXml(_ \\ "fileIdentifier" text).trim()
    val md = CswGetRecordById(fileId, OutputSchemas.IsoRecord).execute()
    (md must haveA200ResponseCode) and
      (md.value.withXml {_ \\ "GetRecordByIdResponse" must not beEmpty})
  }
  
  val cswGetInsertedMdByCswGetRecords = (response:ImportResponseType) => {
    val fileId = response.last.value.withXml(_ \\ "fileIdentifier" text).trim()
    val filter = PropertyIsEqualTo("Identifier", fileId).xml
	val md = CswGetRecordsRequest(filter, outputSchema = OutputSchemas.CheIsoRecord, resultType = ResultTypes.results).execute()
	val xml = md.value.getXml
	(md must haveA200ResponseCode) and
		((xml \\ "SearchResults" \@ "numberOfRecordsReturned").head.trim.toInt must be_>= (1)) and
		(xml \\ "CHE_MD_Metadata" must not beEmpty)
  }
  
  val deleteMetadata = (response:ImportResponseType) => {
    val id = response._1.value
    DeleteMetadata.execute(id)
    success
  }
}