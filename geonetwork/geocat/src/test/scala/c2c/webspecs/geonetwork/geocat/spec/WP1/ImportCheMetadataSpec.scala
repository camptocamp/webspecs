package c2c.webspecs
package geonetwork
package geocat
package spec.WP1

import org.specs2.specification.Step
import c2c.webspecs.geonetwork.ImportStyleSheets.NONE
import c2c.webspecs.geonetwork._
import c2c.webspecs.{XmlValue, Response, IdValue, GetRequest}
import accumulating._

/**
 * Created by IntelliJ IDEA.
 * User: pmauduit
 * Date: 28/06/11
 * Time: 16:06
 */

class ImportCheMetadataSpec  extends GeonetworkSpecification {  def is =

  "This specification tests using the iso19139.CHE schema"                  ^ Step(setup)               ^
    "Inserting a CHE metadata"                                              ^ importISO19139CCHE.toGiven   ^
    "Should suceed with a 200 response"                                     ^ import200Response         ^
    "And the new metadata should be accessible via xml.metadata.get"        ^ getInsertedMd.toThen      ^
    "As well as via csw getRecord"                                          ^ cswGetInsertedMd.toThen      ^
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
    val fileId = response.last.value.withXml(_ \\ "fileIdentifier" text)
    val md = CswGetByFileId(fileId, OutputSchemas.IsoRecord)(None)
    (md must haveA200ResponseCode) and
      (md.value.withXml {_ \\ "Record" must not beEmpty})
  }
}