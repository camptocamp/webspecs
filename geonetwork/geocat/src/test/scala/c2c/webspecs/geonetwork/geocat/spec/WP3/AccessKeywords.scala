package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step
import c2c.webspecs.{XmlValue, Response}
import geonetwork.UserRef

class AccessKeywords extends GeonetworkSpecification { def is =

  "This specification tests accessing shared keyword"              ^ Step(setup) ^
    "Listing all keyword"                                          ^ listKeywords.give ^
      "Should succeed with a 200 response"                         ^ l200Response ^
      "Should show all contain water in one of the translations"   ^ containWater.then    ^
                                                                     end ^
    "Gettings a keyword in iso xml"                                ^ keywordInIso.give  ^
      "Should succeed with a 200 response"                         ^ i200Response      ^
      "Should show translations"                                   ^ isoTranslation.then   ^
                                                                     Step(tearDown)

  type ListResponse = Response[List[KeywordRef]]

  def listKeywords = (_:String) => SearchKeywords(List(GeocatConstants.INSPIRE_THESAURUS))("Hydrograf"):ListResponse
  val l200Response = a200ResponseThen.narrow[ListResponse]
  val containWater = (response:ListResponse, _:String) =>
    response.value.find(_.value contains "Hydrograf") must not be (None)

  val locales = List("fr","en","de")
  val keywordInIso = (_:String) => GetIsoKeyword(GeocatConstants.INSPIRE_THESAURUS, locales)("http://rdfdata.eionet.europa.eu/inspirethemes/themes/26"):Response[IsoKeyword]
  val i200Response = a200ResponseThen.narrow[Response[IsoKeyword]]
  val isoTranslation = (response:Response[IsoKeyword]) => {
    val languages = response.value.labels.keys

    (
      (languages.toList must_== locales) and
      (response.value.labels must havePair("EN" -> "geographic information system"))
    )
  }


}
