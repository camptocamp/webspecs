package c2c.webspecs
package geoserver
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.Matcher
import org.specs2.execute.Result

@RunWith(classOf[JUnitRunner])
class IGNSpec extends GeoserverSpecification {

  def isImpl = 
    "ignGetFeatureTests".title ^
    	"This Spec test WFS for IGN get Feature API" ^
    	"Wfs Count should limit the number of features loaded" ! count ^
    	"Wfs Fid Filter must retrieve exactly one feature" ! fidFilter
    	
    	
   def count = {
    val response = GetWfsRequest("2.0.0", "GetFeature", "typeName" -> "au:AdministrativeUnit", "count" -> 1).execute()
    val xmlData = response.value.getXml
    
    (response must haveA200ResponseCode) and
      (response.value.getXml \\ "member" must haveSize(1))
    
  } 	
   def fidFilter = {
    val filter = <fes:ResourceId rid="FR2100000000"/>
    val response = new GetFeatureRequest("au:AdministrativeUnit", filter).execute()
    println(response.value.getText.take(1000))
    (response must haveA200ResponseCode) and
      (response.value.getXml \\ "member" must haveSize(1))
      
  }
    
  
}