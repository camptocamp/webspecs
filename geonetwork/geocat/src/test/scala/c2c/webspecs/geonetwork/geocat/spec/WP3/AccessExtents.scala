package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step

class AccessExtents extends GeonetworkSpecification { def is =

  "This specification tests accessing shared extent"           ^ Step(setup) ^
    "Searching extends for '${berne}' in ${gmd_bbox} format"   ^ listExtents.give ^
      "Should succeed with a 200 response"                     ^ l200Response ^
      "Should find bern"                                       ^ findBernResult.then    ^
      "Should show href"                                       ^ listhref.then    ^
      "Should show localized desc"                             ^ listLocalizedDesc.then    ^
      "Should have ${gmd_bbox} format in uri"                  ^ formatCheck.then    ^
      "Should indicate validated"                              ^ listValidated.then    ^
                                                                 end^
    "Searching extends for '${be}' in ${gmd_complete} format"  ^ listExtents.give ^
      "Should find bern"                                       ^ findBernResult.then    ^
      "Should have ${gmd_complete} format in uri"              ^ formatCheck.then    ^
                                                                 end ^
    "Gettings a ${gmd_bbox} extent in iso xml"                  ^ extentInIso.give  ^
      "Should succeed with a 200 response"                     ^ i200Response      ^
      "Should show name"                                       ^ bboxExtent.then      ^
      "Should show translations"                               ^ haveDesc.then   ^
                                                                 end ^
    "Gettings a ${gmd_complete} extent in iso xml"                     ^ extentInIso.give  ^
      "Should succeed with a 200 response"                     ^ i200Response      ^
      "Should show name"                                       ^ bboxExtent.then      ^
      "Should show name"                                       ^ polygonExtent.then      ^
      "Should show translations"                               ^ haveDesc.then   ^ end ^
                                                           Step(tearDown)
  type ListResponse = Response[List[ExtentSummary]]

  val listExtents = (s:String) => {
    val (search, format) = extract2(s)
    SearchExtent(numResults = 200, format = ExtentFormat.withName(format))(search):ListResponse
  }
  def findBern[U](response:ListResponse)(mapping: ExtentSummary => U) = response.value.find(_.id == "351").map(mapping)
  val l200Response = a200ResponseThen.narrow[ListResponse]
  val findBernResult = (response:ListResponse, _:String) => response.value.map(_.id) must contain ("351")
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
