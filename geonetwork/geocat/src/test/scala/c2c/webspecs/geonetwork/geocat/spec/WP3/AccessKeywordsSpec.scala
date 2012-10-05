package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step
import org.specs2.execute.Result
import c2c.webspecs.Response
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AccessKeywordsSpec extends GeocatSpecification { def is =

  "This specification tests accessing shared keyword"                           ^ Step(setup) ^
    "Searching  ${"+GeocatConstants.INSPIRE_THESAURUS+"} for keyword ${Hydrograf}"                                  ^ searchForKeyword.toGiven ^
      "Should be a successful http request (200 response code)"                                  ^ l200Response ^
      "Should show all contain ${Hydrograf} in one of the translations"     ^ containKeyword.toThen    ^
                                                                              end ^
    "Searching  ${"+GeocatConstants.GEMET_THESAURUS+"} for keyword ${water}"                                  ^ searchForKeyword.toGiven ^
          "Should be a successful http request (200 response code)"                                ^ l200Response ^
      "Should show all contain ${water} in one of the translations"       ^ containKeyword.toThen    ^
                                                                            end ^
    "Searching  ${"+GeocatConstants.GEMET_THESAURUS+"} for keyword ${WATER}"                                  ^ searchForKeyword.toGiven ^
          "Should be a successful http request (200 response code)"                                ^ l200Response ^
      "Should show all contain ${water} in one of the translations"       ! pending    ^
                                                                            end ^
    "Gettings a keyword in iso xml"                                       ^ keywordInIso.toGiven  ^
      "Should be a successful http request (200 response code)"                                ^ i200Response      ^
      "Should show translations"                                          ^ isoTranslation.toThen   ^
                                                                            Step(tearDown)

  type ListResponse = Response[List[KeywordRef]]

  def searchForKeyword = (string:String) => {
    val (thesaurus,word) = extract2(string)
    SearchKeywords(List(thesaurus)).execute(word):ListResponse
  }
  val l200Response = a200ResponseThen.narrow[ListResponse]
  val containKeyword = (response:ListResponse, s:String) =>
    response.value.filter(_.lang == "eng").foldLeft(success:Result){(acc,next) => acc and (next.value must =~ (extract1(s)))}

  val locales = List("FR","EN","DE")
  val keywordInIso = () => GetIsoKeyword(GeocatConstants.INSPIRE_THESAURUS, locales).execute("http://rdfdata.eionet.europa.eu/inspirethemes/themes/26"):Response[IsoKeyword]
  val i200Response = a200ResponseThen.narrow[Response[IsoKeyword]]
  val isoTranslation = (response:Response[IsoKeyword]) => {
    val languages = response.value.labels.keys
    (
      (languages.toList must haveTheSameElementsAs (locales)) and
      (response.value.labels must havePair("EN" -> "Atmospheric conditions"))
    )
  }


}
