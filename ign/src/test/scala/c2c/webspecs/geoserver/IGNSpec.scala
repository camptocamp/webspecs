package c2c.webspecs
package geoserver
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
    	"Wfs Fid Filter must retrieve exactly one feature" ! fidFilter
    	
    	
   def fidFilter = {
    val filter = <fes:ResourceId rid="FR2100000000"/>
    val response = new GetFeatureRequest("au:AdministrativeUnit", filter).execute()
    println(response.value.getText.take(1000))
    (response must haveA200ResponseCode) and
      (response.value.getXml \\ "member" must haveSize(1))
      
  }
    
  
}