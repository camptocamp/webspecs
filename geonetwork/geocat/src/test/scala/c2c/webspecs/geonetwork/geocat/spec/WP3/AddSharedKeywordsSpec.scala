package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2._
import specification._
import java.util.UUID
import scala.xml.NodeSeq
import geocat.GeocatConstants.NON_VALIDATED_THESAURUS

class AddSharedKeywordsSpec extends GeonetworkSpecification { def is =
  "This specification tests creating shared keyword by passing in a keyword xml snippet"                          ^ Step(setup) ^ t ^
    "Calling shared.process with the xml snippet for adding a keyword"                                           ^ keywordAdd.toGiven ^
    "Should have 200 result"                                                                                    ^ a200ResponseThen.narrow[Response[NodeSeq]] ^
    "Keyword node should have an xlink href"                                                                     ^ hrefInElement.toThen ^
    "xlink href should retrieve the full keyword"                                                                ^ xlinkGetElement.toThen ^
    "Will result in a new shared keyword"                                                                        ! newKeyword ^
                                                                                                                  end ^
    "Deleted new keyword"                                                                                        ^ Step(deleteNewKeyword) ^
    "Must correctly delete said keyword"                                                                         ! noKeyword
                                                                                                                  Step(tearDown)

  val keywordAdd = () => (config.adminLogin then ProcessSharedObject(keywordXML))(None)
  val hrefInElement = (result:Response[NodeSeq]) => (result.value \\ "descriptiveKeywords" \@ "xlink:href") must not beEmpty
  val xlinkGetElement = (result:Response[NodeSeq]) => {
    val href = (result.value \\ "descriptiveKeywords" \@ "xlink:href")(0)
    val xlink = GetRequest(href)(None)
    (xlink must haveA200ResponseCode) and
      haveCorrectTranslation (xlink, "#DE", deValue) and
      haveCorrectTranslation (xlink, "#EN", enValue) and
      haveCorrectTranslation (xlink, "#FR", frValue)

  }

  def haveCorrectTranslation(xlink:Response[XmlValue], locale:String,expected:String) =
    xlink.value.withXml{xml =>
      val nodeName = "LocalisedCharacterString"
      val repr = (xml \\ nodeName)+" @ "+locale
      xml \\ nodeName find {_ @@ "locale" == Some(locale)} map (_.text) aka repr must beSome(expected)}

  def Search = SearchKeywords(List(NON_VALIDATED_THESAURUS))
  def newKeyword = Search(deValue).value.find(_.value == deValue) must beSome

  def deleteNewKeyword = Search(deValue).value.foreach{c => DeleteKeyword(NON_VALIDATED_THESAURUS,"",c.id)}
  def noKeyword = Search(deValue).value must beEmpty
  lazy val uuid = UUID.randomUUID().toString
  lazy val deValue = uuid+"de*automated*"
  lazy val enValue = uuid+"en*automated*"
  lazy val frValue = uuid+"fr*automated*"
  lazy val keywordXML =
    <gmd:descriptiveKeywords  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gmd="http://www.isotc211.org/2005/gmd">
        <gmd:MD_Keywords>
          <gmd:keyword xsi:type="gmd:PT_FreeText_PropertyType">
            <gmd:PT_FreeText>
              <gmd:textGroup>
                <gmd:LocalisedCharacterString locale="#DE">{deValue}</gmd:LocalisedCharacterString>
              </gmd:textGroup>
              <gmd:textGroup>
                <gmd:LocalisedCharacterString locale="#EN">{enValue}</gmd:LocalisedCharacterString>
              </gmd:textGroup>
              <gmd:textGroup>
                <gmd:LocalisedCharacterString locale="#FR">{frValue}</gmd:LocalisedCharacterString>
              </gmd:textGroup>
            </gmd:PT_FreeText>
          </gmd:keyword>
        </gmd:MD_Keywords>
      </gmd:descriptiveKeywords>

}