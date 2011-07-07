package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step

class AccessExtentsSpec extends GeonetworkSpecification { def is =

  "This specification tests accessing shared extent"           ^ Step(setup) ^
    "Searching extends for '${berne}' in ${gmd_bbox} format"   ^ listExtents.toGiven ^
      "Should succeed with a 200 response"                     ^ l200Response ^
      "Should find ${Bern}"                                    ^ findExtentResult.toThen    ^
      "Should show href"                                       ^ listhref.toThen    ^
      "Should show localized desc"                             ^ listLocalizedDesc.toThen    ^
      "Should have ${gmd_bbox} format in uri"                  ^ formatCheck.toThen    ^
      "Should indicate validated"                              ^ listValidated.toThen    ^
                                                                 end^
    "Searching extends for '${be}' in ${gmd_complete} format"  ^ listExtents.toGiven ^
      "Should find ${Bern}"                                    ^ findExtentResult.toThen    ^
      "Should have ${gmd_complete} format in uri"              ^ formatCheck.toThen    ^
                                                                 end ^
    "Searching extends for '${Be}' in ${gmd_polygon} format"  ^ listExtents.toGiven ^
      "Should find ${Bern}"                                   ^ findExtentResult.toThen    ^
      "Should have ${gmd_polygon} format in uri"              ^ formatCheck.toThen    ^
                                                                 end ^
    "Searching extends for '${Graubünden}' in ${gmd_polygon} format"  ^ listExtents.toGiven ^
      "Should find ${Graubünden}"                              ^ findExtentResult.toThen    ^
                                                                 end ^
    "Gettings a ${gmd_bbox} extent in iso xml"                 ^ extentInIso.toGiven  ^
      "Should succeed with a 200 response"                     ^ i200Response      ^
      "Should have bbox Extent"                                ^ bboxExtent.toThen      ^
      "Should have description"                                ^ haveDesc.toThen   ^
                                                                 end ^
    "Gettings a ${gmd_complete} extent in iso xml"             ^ extentInIso.toGiven  ^
      "Should succeed with a 200 response"                     ^ i200Response      ^
      "Should have bbox Extent"                                ^ bboxExtent.toThen      ^
      "Should have polygon extent"                             ^ polygonExtent.toThen      ^
      "Should have description"                                ^ haveDesc.toThen   ^ end ^
                                                           Step(tearDown)
  type ListResponse = Response[List[ExtentSummary]]

  val listExtents = (s:String) => {
    val (search, format) = extract2(s)
    SearchExtent(numResults = 200, format = ExtentFormat.withName(format))(search):ListResponse
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
    (uri must contain("http://localhost:8080/geonetwork/srv/fr/xml.extent.get?wfs=default&format=")) and
    (uri must contain("&typename=gn:gemeindenBB&id=351"))
  }
  val listLocalizedDesc = (response:ListResponse, _:String) => {
    val bernDesc = findBern(response)(_.desc).get
    val fr = bernDesc.translation.get("fr")
    (
      (bernDesc.translation.keys must contain("fr","en","de","it")) and
      (fr must beSome( "Berne"))
    )
  }
  val formatCheck = (response:ListResponse, s:String) => findBern(response)(_.href.toString).get.toLowerCase must contain("format="+extract1(s))
  val listValidated = (response:ListResponse) => findBern(response)(_.validated) must beSome(true)

  val extentInIso = (s:String) => GetRequest("xml.extent.get",
    "wfs" -> "default",
    "format" -> extract1(s),
    "id" -> 351,
    "typename" -> "gn:gemeindenBB")(None):Response[XmlValue]
  val i200Response = a200ResponseThen.narrow[Response[XmlValue]]
  val bboxExtent = (response:Response[XmlValue]) => response.value.withXml{ _ must \\("EX_GeographicBoundingBox") }
  val polygonExtent = (response:Response[XmlValue]) => response.value.withXml{ _ must \\("EX_BoundingPolygon") }
  val haveDesc = (response:Response[XmlValue]) => response.value.withXml{ _ must \\("description") }

}
