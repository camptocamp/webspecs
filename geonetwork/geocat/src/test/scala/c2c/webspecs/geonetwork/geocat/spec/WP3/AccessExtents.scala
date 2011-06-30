package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step

class AccessExtents extends GeonetworkSpecification { def is =

  "This specification tests accessing shared extent"     ^ Step(setup) ^
    "Listing all extents"                                ^ listExtents.give ^
      "Should succeed with a 200 response"               ^ l200Response ^
      "Should show href"                                 ^ listhref.then    ^
      "Should show localized desc"                       ^ listLocalizedDesc.then    ^
      "Should show localized desc"                       ^ listId.then    ^
      "Should show localized desc"                       ^ listValidated.then    ^
                                                         end ^
    "Gettings a extent in iso xml"                       ^ extentInIso.give  ^
      "Should succeed with a 200 response"               ^ i200Response      ^
      "Should show name"                                 ^ isoName.then      ^
      "Should show translations"                         ^ isoLastName.then   ^
                                                           Step(tearDown)
  type ListResponse = Response[List[ExtentSummary]]

  def listExtents = (_:String) => SearchExtent()("be"):ListResponse
  val l200Response = a200ResponseThen.narrow[ListResponse]
  val listhref = (response:ListResponse, _:String) => pending
  val listLocalizedDesc = (response:ListResponse, _:String) => pending
  val listId = (response:ListResponse, _:String) => pending
  val listValidated = (response:ListResponse, _:String) => pending

  val extentInIso = (_:String) => GetRequest("xml.extent.get",
    "id" -> "http://rdfdata.eionet.europa.eu/inspirethemes/themes/26",
    "thesaurus" -> GeocatConstants.INSPIRE_THESAURUS,
    "multiple" -> false)(None):Response[XmlValue]
  val i200Response = a200ResponseThen.narrow[Response[XmlValue]]
  val isoName = (response:Response[XmlValue], _:String) => pending
  val isoLastName = (response:Response[XmlValue], _:String) => pending

}
