package c2c.webspecs
package geoserver
import c2c.webspecs.WebSpecsSpecification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.Matcher
import org.specs2.execute.Result

@RunWith(classOf[JUnitRunner])
class IGNSpec extends GeoserverSpecification {
  // TODO test WFSDispatcher with all of these as well as just /wfs
  def isImpl = 
    "ignGetFeatureTests".title ^
    	"This Spec test WFS for IGN get Feature API" ^
    	"Wfs getCapabilities 1.0.0 must be valid" ! minimumXPath
    	
    	
   def minimumXPath = {
    val response = new GetFeatureRequest("au:AdministrativeUnit", <fes:ResourceId rid="FR2100000000">
    </fes:ResourceId>).execute()
    println(response.value.getXml)
    (response must haveA200ResponseCode) and
      (response.value.getXml must \\("Capability"))
      
  }
    
  
}