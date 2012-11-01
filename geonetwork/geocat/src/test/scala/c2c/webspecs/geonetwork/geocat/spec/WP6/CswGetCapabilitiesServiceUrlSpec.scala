package c2c.webspecs
package geonetwork
package geocat
package spec.WP6

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step

import c2c.webspecs.geonetwork.csw._
import c2c.webspecs.geonetwork.UserProfiles


@RunWith(classOf[JUnitRunner])
class CswGetCapabilitiesServiceUrlSpec extends GeocatSpecification {
	def is = {
	  "CSW GetCapabilities services URL".title 	^ Step(setup) ^
           "Checking GetCapabilities services URL in ${fre}" ! CswTestGetCapabilities ^
           "Checking GetCapabilities services URL in ${ger}" ! CswTestGetCapabilities ^
	  	   "Checking GetCapabilities services URL in ${eng}" ! CswTestGetCapabilities ^
	  	   end ^ Step(tearDown)								
	}
    
    def CswTestGetCapabilities = (description : String) => {
      val languageCode = extract1(description)
      val cswRequest = CswGetCapabilities(url= languageCode+"/csw").execute().value.getXml
                                        
      val serviceUrlGet = cswRequest \\ "Capabilities" \\ "Operation" \ "DCP" \ "HTTP" \ "Get"  \@ "xlink:href"
      val serviceUrlPost = cswRequest \\ "Capabilities" \\ "Operation" \ "DCP" \ "HTTP" \ "Post"  \@ "xlink:href"

      (serviceUrlGet ++ serviceUrlPost) must contain ("srv/"+languageCode+"/csw").foreach
    }   
    def CswTestGetCapabilitiesMustNotFail = (description : String) => {
      val languageCode = extract1(description)
      val cswRequest = CswGetCapabilities(url= languageCode+"/csw").execute().value.getXml
                                        
      val serviceUrlGet = cswRequest \\ "Capabilities" \\ "Operation" \ "DCP" \ "HTTP" \ "Get"  \@ "xlink:href"
      val serviceUrlPost = cswRequest \\ "Capabilities" \\ "Operation" \ "DCP" \ "HTTP" \ "Post"  \@ "xlink:href"

      (serviceUrlGet must not beEmpty) and
        (serviceUrlPost must not beEmpty)
      
    }
	
	
}