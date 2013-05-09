package c2c.webspecs
package geonetwork
package spec.csw

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step

import c2c.webspecs.geonetwork.csw._

@RunWith(classOf[JUnitRunner])
class CswGetCapabilitiesServiceUrlSpec extends GeonetworkSpecification {
  def is = {
    "CSW GetCapabilities services URL".title ^ Step(setup) ^
      "Checking GetCapabilities services URL in ${fre}" ! CswTestGetCapabilities ^
      "Checking GetCapabilities services URL in ${ger}" ! CswTestGetCapabilities ^
      "Checking GetCapabilities services URL in ${eng}" ! CswTestGetCapabilities ^
      end ^ Step(tearDown)
  }

  def CswTestGetCapabilities = (description: String) => {
    val languageCode = extract1(description)
    val cswRequest = CswGetCapabilities(url = "http://" + Properties.testServer + "/geonetwork/srv/" + languageCode + "/csw?language="+languageCode).execute().value.getXml

    val serviceUrlGet = cswRequest \\ "Capabilities" \\ "Operation" \ "DCP" \ "HTTP" \ "Get" \@ "xlink:href"
    val serviceUrlPost = cswRequest \\ "Capabilities" \\ "Operation" \ "DCP" \ "HTTP" \ "Post" \@ "xlink:href"

    (serviceUrlGet ++ serviceUrlPost) must contain("srv/" + languageCode + "/csw").foreach
  }


}