package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AccessExtentsSpec extends GeocatSpecification { def is =

  "This specification tests accessing shared extent"           ^ Step(setup) ^
    "Searching extents for '${berne}' in ${gmd_bbox} format"   ^ listExtents.toGiven ^
      "Should be a successful http request (200 response code)"                     ^ l200Response ^
      "Should find ${Bern}"                                    ^ findExtentResult.toThen    ^
      "Should show href"                                       ^ listhref.toThen    ^
      "Should show localized desc"                             ^ listLocalizedDesc.toThen    ^
      "Should have ${gmd_bbox} format in uri"                  ^ formatCheck.toThen    ^
      "Should indicate validated"                              ^ listValidated.toThen    ^
                                                                 end^
    "Searching extents for '${be}' in ${gmd_complete} format"  ^ listExtents.toGiven ^
      "Should find ${Bern}"                                    ^ findExtentResult.toThen    ^
      "Should have ${gmd_complete} format in uri"              ^ formatCheck.toThen    ^
                                                                 end ^
    "Searching extents for '${Be}' in ${gmd_polygon} format"  ^ listExtents.toGiven ^
      "Should find ${Bern}"                                   ^ findExtentResult.toThen    ^
      "Should have ${gmd_polygon} format in uri"              ^ formatCheck.toThen    ^
                                                                 end ^
    "Searching extents for '${Graubünden}' in ${gmd_polygon} format"  ^ listExtents.toGiven ^
      "Should find ${Graubünden}"                              ^ findExtentResult.toThen    ^
                                                                 end ^
    "Gettings a ${gmd_bbox} extent in iso xml"                 ^ extentInIso.toGiven  ^
      "Should be a successful http request (200 response code)"                     ^ i200Response      ^
      "Should have bbox Extent"                                ^ bboxExtent.toThen      ^
      "Should have description"                                ^ haveDesc.toThen   ^
      "Should have localised strings but not character Strings"^ haveLocalisedDesc.toThen   ^
                                                                 end ^
    "Gettings a ${gmd_complete} extent in iso xml"             ^ extentInIso.toGiven  ^
      "Should be a successful http request (200 response code)"                     ^ i200Response      ^
      "Should have bbox Extent"                                ^ bboxExtent.toThen      ^
      "Should have polygon extent"                             ^ polygonExtent.toThen      ^
      "Should have description"                                ^ haveDesc.toThen   		  ^ 
      "Should have localised strings but not character Strings"^ haveLocalisedDesc.toThen ^ end ^
                                                           Step(tearDown)
  type ListResponse = Response[List[ExtentSummary]]

  val listExtents = (s:String) => {
    val (search, format) = extract2(s)
    SearchExtent(numResults = 10000, format = ExtentFormat.withName(format)).execute(search):ListResponse
  }
  def findBern[U](response:ListResponse)(mapping: ExtentSummary => U) = response.value.find(_.id == "351").map(mapping)
  val l200Response = a200ResponseThen.narrow[ListResponse]
  val findExtentResult = (response:ListResponse, s:String) => {
    val id = extract1(s).toLowerCase match {
      case "bern" => "351"
      case "graubünden" => "18"
    }
    response.value.map(_.id) must contain (id)
  }
  val listhref = (response:ListResponse, _:String) => {
    val uri = findBern(response)(_.href.toString).get
    (uri must contain("/xml.extent.get?")) and
    (uri must contain("wfs=default")) and
    (uri must contain("id=351")) and
    (uri must contain("typename=gn:gemeindenBB"))
  }
  val listLocalizedDesc = (response:ListResponse, _:String) => {
    val bernDesc = findBern(response)(_.desc).get
    val fr = bernDesc.translations.get("fr")
    (
      (bernDesc.translations.keys must contain("fr","en","de","it")) and
      (fr must beSome( "Berne"))
    )
  }
  val formatCheck = (response:ListResponse, s:String) => findBern(response)(_.href.toString).get.toLowerCase must contain("format="+extract1(s))
  val listValidated = (response:ListResponse) => findBern(response)(_.validated) must beSome(true)

  val extentInIso = (s:String) => GetRequest("xml.extent.get",
    "wfs" -> "default",
    "format" -> extract1(s),
    "id" -> 351,
    "typename" -> "gn:gemeindenBB").execute():Response[XmlValue]
  val i200Response = a200ResponseThen.narrow[Response[XmlValue]]
  val bboxExtent = (response:Response[XmlValue]) => response.value.withXml{ _ must \\("EX_GeographicBoundingBox") }
  val polygonExtent = (response:Response[XmlValue]) => response.value.withXml{ _ must \\("EX_BoundingPolygon") }
  val haveDesc = (response:Response[XmlValue]) => response.value.withXml{ _ must \\("description") }
  val haveLocalisedDesc = (response:Response[XmlValue]) => {
    val xml = response.value.getXml
    val desc = xml \\ "description"
    val characterStrings =  desc \\ "CharacterString"
    val localisedStrings =  desc \\ "LocalisedCharacterString"
    
    (characterStrings must beEmpty) and
    	(localisedStrings must haveSize(4))
  }

}
