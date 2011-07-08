package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2._
import specification._
import java.util.UUID
import scala.xml.NodeSeq

class AddSharedFormatSpec extends GeonetworkSpecification { def is =
  "This specification tests creating shared format by passing in a format xml snippet"                          ^ Step(setup) ^ t ^
    "Calling shared.process with the xml snippet for adding a format"                                           ^ formatAdd.toGiven ^
    "Should have 200 result"                                                                                    ^ a200ResponseThen.narrow[Response[NodeSeq]] ^
    "Format node should have an xlink href"                                                                     ^ hrefInElement.toThen ^
    "xlink href should retrieve the full format"                                                                ^ xlinkGetElement.toThen ^
    "Will result in a new shared format"                                                                        ! newFormat  ^
                                                                                                                  end ^
    "Deleting new format"                                                                                       ^ Step(deleteNewFormat) ^
    "Must correctly remove that format from the system"                                                         ! noFormat ^
                                                                                                                  Step(tearDown)

  val formatAdd = () => (config.adminLogin then ProcessSharedObject(formatXML))(None)
  val hrefInElement = (result:Response[NodeSeq]) => (result.value \\ "resourceFormat" \@ "xlink:href") must not beEmpty
  val xlinkGetElement = (result:Response[NodeSeq]) => {
    val href = (result.value \\ "resourceFormat" \@ "xlink:href")(0)
    val xlink = GetRequest(href)(None)
    (xlink must haveA200ResponseCode) and
      (xlink.value.withXml{_ \\ "name" map (_.text.trim) must contain (formatName)})
  }

  def newFormat = ListFormats(formatName).value.find(_.name == formatName) must beSome

  def deleteNewFormat = ListFormats(formatName).value.foreach{c => DeleteFormat(c.id)}
  def noFormat = ListFormats(formatName).value must beEmpty

  lazy val uuid = UUID.randomUUID().toString
  lazy val formatName = uuid+"name*automated*"
  lazy val version = uuid+"version*automated*"
  lazy val formatXML = <gmd:resourceFormat xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gmd="http://www.isotc211.org/2005/gmd">
        <gmd:MD_Format>
          <gmd:name>
            <gco:CharacterString>{formatName}</gco:CharacterString>
          </gmd:name>
          <gmd:version>
            <gco:CharacterString>{version}</gco:CharacterString>
          </gmd:version>
        </gmd:MD_Format>
      </gmd:resourceFormat>

}