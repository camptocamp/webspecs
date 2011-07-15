package c2c.webspecs
package geonetwork
package geocat
package spec.WP1

import org.specs2.specification.Step
import c2c.webspecs.geonetwork.ImportStyleSheets.NONE
import c2c.webspecs.geonetwork._
import c2c.webspecs.{XmlValue, Response, IdValue, GetRequest}
import accumulating._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner


// TODO : finish to implement this test ...


@RunWith(classOf[JUnitRunner]) 
class TestXlinksUpdate  extends GeonetworkSpecification {  def is =

  "This specification tests using the iso19139.CHE schema"                  ^ Step(setup)               ^
    "Inserting a CHE metadata"                                              ^ importISO19139CCHE.toGiven   ^
    "Should suceed with a 200 response"                                     ^ import200Response         ^
    "And the new metadata should be accessible via xml.metadata.get"        ^ getInsertedMd.toThen      ^
    "As well as via csw getRecordById"                                      ^ cswGetInsertedMd.toThen      ^
    "As well as via csw getRecords"                                          ^ cswGetInsertedMdByCswGetRecords.toThen      ^
                                                                            Step(tearDown)

  type ImportResponseType = AccumulatedResponse1[IdValue, XmlValue]

  val importISO19139CCHE = (_:String) => {
    val name = "metadata.iso19139.che.xml"
    val (_,content) = ImportMetadata.importDataFromClassPath("/data/"+name, getClass)
    val ImportMd = ImportMetadata.findGroupId(content,NONE,true)
    val GetMdRequest = (resp:Response[IdValue]) => GetRequest("xml.metadata.get", "id" -> resp.value.id)

    (ImportMd startTrackingThen GetMdRequest)(ImportStyleSheets.NONE):ImportResponseType
  }

  val import200Response = a200ResponseThen.narrow[ImportResponseType]


  val getInsertedMd = (response:ImportResponseType) => {
    val xmlResponse = response.last

    xmlResponse.value.withXml{md =>

      val node = (md \\ "citation"  \ "CI_Citation" \ "title" \ "CharacterString").text

      val abstractText = (md \\ "abstract" \\ "CharacterString").text.trim
      val abstractLocalisedENtext = (md \\ "abstract" \\ "LocalisedCharacterString" find (n =>  (n \\ "@locale").text == "#EN")).get.text
      val abstractLocalisedDEtext = (md \\ "abstract" \\ "LocalisedCharacterString" find (n =>  (n \\ "@locale").text == "#DE")).get.text

      // Test che: fields
      val cheLocalisedUrl = (md \ "contact" \ "CHE_CI_ResponsibleParty" \ "contactInfo" \ "CI_Contact" \
        "onlineResource" \ "CI_OnlineResource" \ "linkage" \ "PT_FreeURL" \ "URLGroup" \ "LocalisedURL").text

//        ((node must_== "COmprehenisve Test") and (abstractText must_== "xx"))
        ((node must_== "FR Title")
          and (abstractText must_== "FR abstract")
          and (cheLocalisedUrl must_== "http://www.awnl.llv.li")
          and (abstractLocalisedENtext must_== "EN  abstract")
          and (abstractLocalisedDEtext must_== "DE  abstract")
          )
    }
  }

  val cswGetInsertedMd = (response:ImportResponseType) => {
    val fileId = response.last.value.withXml(_ \\ "fileIdentifier" text).trim()
    val md = CswGetByFileId(fileId, OutputSchemas.IsoRecord)(None)
    (md must haveA200ResponseCode) and
      (md.value.withXml {_ \\ "GetRecordByIdResponse" must not beEmpty})
  }
  
  val cswGetInsertedMdByCswGetRecords = (response:ImportResponseType) => {
    val fileId = response.last.value.withXml(_ \\ "fileIdentifier" text).trim()
    val filter = PropertyIsEqualTo("Identifier", fileId).xml
	val md = CswGetRecordsRequest(filter, outputSchema = OutputSchemas.IsoRecord, resultType = ResultTypes.results)(None)
	val xml = md.value.getXml
	(md must haveA200ResponseCode) and
		((xml \\ "SearchResults" \@ "numberOfRecordsReturned").head.trim.toInt must be_>= (1)) and
		(xml \\ "CHE_MD_Metadata" must not beEmpty)
  }
}