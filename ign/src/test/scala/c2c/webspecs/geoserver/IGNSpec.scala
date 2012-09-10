package c2c.webspecs
package geoserver
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.Matcher
import org.specs2.execute.Result
import _root_.scala.xml.Text

@RunWith(classOf[JUnitRunner])
class IGNSpec extends GeoserverSpecification {

  def isImpl = 
    "ignGetFeatureTests".title ^
    	"This Spec test WFS for IGN get Feature API" ^
    	"Wfs Count should limit the number of features loaded" ! count ^
    	"Wfs Fid Filter must retrieve exactly one feature" ! fidFilter ^
    	"Wfs equalTo Filter must retrieve exactly 9 feature" ! equalToFilter^
    	"Wfs equalTo Filter with XPath must retrieve exactly 9 feature" ! xPathAttributeFilter^
    	"GetPropertyValue should return only the value condominium" ! getPropertyValue^
    	"administrativeBoundary should respect schema" ! abNoResolve
    	
    	
   def count = {
    val response = GetWfsRequest("2.0.0", "GetFeature", "typeName" -> "au:AdministrativeUnit", "count" -> 1).execute()
    val xmlData = response.value.getXml
    
    (response must haveA200ResponseCode) and
      (response.value.getXml \\ "member" must haveSize(1))
  } 
  
   def fidFilter = {
    val filter = <fes:ResourceId rid="au.FR2100000000"/>
    val response = new GetFeatureRequest("au:AdministrativeUnit", filter).execute()
    println(response.value.getText.take(1000))
    (response must haveA200ResponseCode) and
      (response.value.getXml \\ "member" must haveSize(1))
      
  }
   
   def equalToFilter = {
    val filter = <fes:PropertyIsEqualTo>
               <fes:ValueReference>au:country/gmd:Country</fes:ValueReference>
               <fes:Literal>FR</fes:Literal>
            </fes:PropertyIsEqualTo>
    val response = new GetFeatureRequest("au:AdministrativeBoundary", filter).execute()
    println(response.value.getText.take(1000))
    (response must haveA200ResponseCode) and
      (response.value.getXml \\ "member" must haveSize(9))
  }
   
   def xPathAttributeFilter = {
    val filter = <fes:PropertyIsEqualTo>
              <fes:ValueReference>au:country/gmd:Country/@codeListValue</fes:ValueReference>
               <fes:Literal>FR</fes:Literal>
            </fes:PropertyIsEqualTo>
    val response = new GetFeatureRequest("au:AdministrativeBoundary", filter).execute()
    println(response.value.getText.take(1000))
    (response must haveA200ResponseCode) and
      (response.value.getXml \\ "member" must haveSize(9))
      
  }
    def getPropertyValue = {
    val response = GetWfsRequest("2.0.0", "GetPropertyValue", "typeName" -> "au:AdministrativeUnit", "count" -> 1, "valueReference" -> "au:condominium").execute()
    val xmlData = response.value.getXml
    val xmlMember = xmlData \\ "member"
    
    (response must haveA200ResponseCode) and
      (xmlData \\ "member" must haveSize(10)) and
         (xmlMember \\ "condominium" must haveSize(10)) and 
         (xmlMember \\ "inspireid" must haveSize(0)) and 
         (xmlMember \\ "nationalcode" must haveSize(0))
    
  } 
    def abNoResolve = {
    val response = GetWfsRequest("2.0.0", "GetFeature", "typeName" -> "au:AdministrativeBoundary").execute()
    //val xmlData = (response.value.getXml \\ "member") filter (x => (x \\ "admUnit" \ "@href" contains Text("FR2100000000")))
    val xmlData = (response.value.getXml \\ "member")(0)
    		
    (response must haveA200ResponseCode) and
      (xmlData \ "AdministrativeBoundary" must haveSize(1)) and
      (xmlData \\ "geometry" must haveSize(1)) and
      (xmlData \\ "inspireId" must haveSize(1)) and
      (xmlData \\ "nationalLevel" must haveSize(1)) and
      (xmlData \\ "legalStatus" must haveSize(1)) and
      (xmlData \\ "technicalStatus" must haveSize(1)) and
      (xmlData \\ "beginLifespanVersion" must haveSize(1)) and
      (xmlData \\ "endLifespanVersion" must haveSize(1)) and
      (xmlData \\ "admUnit" must haveSize(1)) and
      ((xmlData \\ "admUnit" \ "@href").text must contain("AdministrativeUnit")) 
          
  } 
  
}