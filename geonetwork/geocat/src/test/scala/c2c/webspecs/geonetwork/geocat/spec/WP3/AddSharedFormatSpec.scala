package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import shared._
import org.specs2._
import specification._
import scala.xml.NodeSeq
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AddSharedFormatSpec extends GeocatSpecification { def is =
  "This specification tests creating shared format by passing in a format xml snippet"                          ^ Step(setup) ^ t ^
    "Calling shared.process with the xml snippet for adding a format"                                           ^ formatAdd(version).toGiven ^
    "Should be a successful http request (200 response code)"                                                   ^ a200ResponseThen.narrow[Response[NodeSeq]] ^
    "Format node should have an xlink href"                                                                     ^ hrefInElement("resourceFormat").toThen ^
    "Should have the correct ${host} in the xlink created during processing of shared object"                   ^ hrefHost("resourceFormat").toThen ^ 
    "xlink href should retrieve the full format"                                                                ^ xlinkGetElement.toThen ^
    "Will result in a new shared format"                                                                        ! newFormat  ^
                                                                                                                  endp ^
    "Updating an existing format with new XML which has a new version"				                            ^ updateFormat.toGiven ^
      "Should be a successful http request (200 response code)"                                                 ^ a200ResponseThen.narrow[Response[Format]] ^
      "must result in the format retrieved from the xlink also have the new version"                            ^ hasNewVersion.toThen ^
                                                                                                                  endp^
    "Adding same format should return same xlink"										                        ^ Step(formatAdd(updatedVersion)) ^
      "and the xlink should return same format"											                        ! sameFormat ^
                                                                                                                  endp^
    "Deleting new format"                                                                                       ^ Step(deleteNewFormat) ^
    "Must correctly remove that format from the system"                                                         ! noFormat ^
                                                                                                                  Step(tearDown)

  def formatAdd(version:String) = () => (config.adminLogin then ProcessSharedObject(formatXML(version))).execute()
  val xlinkGetElement = (result:Response[NodeSeq]) => {
    val href = (result.value \\ "resourceFormat" \@ "xlink:href")(0)
    val xlink = ResolveXLink.execute(href)
    (xlink must haveA200ResponseCode) and
      (xlink.value.withXml{_ \\ "name" map (_.text.trim) must contain (formatName)})
  }

  def newFormat = ListFormats.execute(formatName).value.find(_.name == formatName) must beSome

  val updateFormat = () => {
    val id = ListFormats.execute(formatName).value.find(_.name == formatName).get.id
    val xml = 
      <gmd:resourceFormat
    		xmlns:xlink="http://www.w3.org/1999/xlink" 
    		xlink:href={"http://localhost:8080/geonetwork/srv/eng/xml.format.get?id="+id}
    		xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gmd="http://www.isotc211.org/2005/gmd">
    	{formatXML(updatedVersion).child}
      </gmd:resourceFormat>
    val updateResponse = (config.adminLogin then UpdateSharedObject(xml)).execute()
    val listResponse = ListFormats.setIn(formatName).execute()
    assert(listResponse.value.size == 1, "A unique format was expected")
    updateResponse.map{_ => listResponse.value.head}
  }
  
  val hasNewVersion = (resp:Response[Format]) => {
    resp.value.version must_== updatedVersion
  }
  
  def sameFormat = {
     val formats = ListFormats.execute(formatName).value
     (formats must haveSize (1)) and
     	(formats.head.name must_== formatName) and
     	(formats.head.version must_== updatedVersion)
  }
  
  def deleteNewFormat = ListFormats.execute(formatName).value.foreach{c => DeleteFormat(true).execute(c.id)}
  def noFormat = ListFormats.execute(formatName).value must beEmpty

  val formatName = uuid+"name*automated*"
  val version = uuid+"version*automated*"
  val updatedVersion = uuid+"newVersion"
  def formatXML(version:String) = <gmd:resourceFormat xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gmd="http://www.isotc211.org/2005/gmd">
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