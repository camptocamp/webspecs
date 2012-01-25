package c2c.webspecs
package geonetwork
package geocat
package spec.WP6

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step

import c2c.webspecs.geonetwork.csw._
import c2c.webspecs.GetRequest
import c2c.webspecs.geonetwork.UserProfiles
import c2c.webspecs.geonetwork.ImportMetadata
import c2c.webspecs.ResourceLoader
import c2c.webspecs.geonetwork.GetRawMetadataXml


@RunWith(classOf[JUnitRunner]) 
class CswGetCapabilitiesServiceUrlSpec extends GeocatSpecification(UserProfiles.Editor) {
	def is = {
	  "CSW GetCapabilities services URL".title 	^ Step(setup) ^
	  	   "Checking GetCapabilities services URL in ${fra}" ! CswTestGetCapabilities ^
	  	   "Checking GetCapabilities services URL in ${deu}" ! CswTestGetCapabilities ^
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
	
	
}