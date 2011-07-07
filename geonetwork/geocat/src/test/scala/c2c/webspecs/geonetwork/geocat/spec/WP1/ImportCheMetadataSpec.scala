package c2c.webspecs.geonetwork.geocat.spec.WP1

import org.specs2.specification.Step
import c2c.webspecs.geonetwork.ImportStyleSheets.NONE
import c2c.webspecs.geonetwork._
import c2c.webspecs.{XmlValue, Response, IdValue, GetRequest}

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
       "Should return the MD"                                               ^ getInsertedMd.toThen      ^
                                                                            Step(tearDown)

  val importISO19139CCHE = (_:String) => {
    val name = "metadata.iso19139.che.xml"
    val (_,content) = ImportMetadata.importDataFromClassPath("/data/"+name, getClass)
    val ImportMd = ImportMetadata.findGroupId(content,NONE,true)

    ImportMd(ImportStyleSheets.NONE):Response[IdValue]
  }
  val GetMetadataXml = () => null.asInstanceOf[Response[XmlValue]]

  val import200Response = a200ResponseThen.narrow[Response[IdValue]]


  val getInsertedMd = (idRes:Response[IdValue]) => {
    val xmlResponse = GetRequest("xml.metadata.get", "id" -> idRes.value.id)(None)
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
}