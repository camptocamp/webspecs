package c2c.webspecs
package geoserver
import c2c.webspecs.WebSpecsSpecification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner]) 
class WFSSpec extends GeoserverSpecification { def isImpl =
  "WFS Spec".title ^
  "This Spec test WFS API" ^
  "Wfs getCapabilities ${1.0.0}" ! capabilities ^
  "Wfs getCapabilities ${1.1.0}" ! capabilities
  
  def capabilities = (s:String) => {
		val version = extract1(s)
		val response = GetRequest("wfs?service=WFS&version="+version+"&REQUEST=GetCapabilities")()
		(response must haveA200ResponseCode) and
			(response.value.getXml must \\("Capability") or \\("WFS_Capabilities")) and 
			(response.value.getXml must \\("GetCapabilities")) and 
			(response.value.getXml must \\("DescribeFeatureType")) and 
			(response.value.getXml must \\("GetFeature")) and 
			(response.value.getXml must \\("Transaction")) and 
			(response.value.getXml must \\("LockFeature")) and 
			(response.value.getXml must \\("GetFeatureWithLock")) and 
			(response.value.getXml must \\("FeatureTypeList")) and 
			(response.value.getXml must \\("FeatureType")) and 
			(response.value.getXml must \\("Filter_Capabilities")) 
  }

}