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
    "IGN GetFeature Tests".title ^
    	"This Spec test WFS for IGN get Feature API" ^
    	"Wfs Count should limit the number of features loaded" ! count ^
    	"Wfs Fid Filter must retrieve exactly one feature" ! fidFilter ^
    	"Wfs equalTo Filter must retrieve exactly 9 feature" ! equalToFilter^
    	"Wfs equalTo Filter with XPath must retrieve exactly 9 feature" ! xPathAttributeFilter^
    	"GetPropertyValue should return only the value condominium" ! getPropertyValue^
    	"FilterCapabilties must have ${ImplementsMinTemporalFilter} as true" ! hasFilterCapability^
    	"administrativeBoundary should respect schema" ! abNoResolve^
    	"administrativeUnit should respect schema" ! auNoResolve^
    	"administrativeBoundary should include 1 unit" ! abResolve1
    	
    	
   def count = {
    val response = GetWfsRequest("2.0.0", "GetFeature", "typeName" -> "au:AdministrativeUnit", "count" -> 1).execute()
    val xmlData = response.value.getXml
    
    (response must haveA200ResponseCode) and
      (response.value.getXml \\ "member" must haveSize(1))
  } 
  
   def fidFilter = {
    val filter = <fes:ResourceId rid="FR2100000000"/>
    val response = new GetFeatureRequest("au:AdministrativeUnit", filter).execute()
    (response must haveA200ResponseCode) and
      (response.value.getXml \\ "member" must haveSize(1))
      
  }
   
   def equalToFilter = {
    val filter = <fes:PropertyIsEqualTo>
               <fes:ValueReference>au:country/gmd:Country</fes:ValueReference>
               <fes:Literal>FR</fes:Literal>
            </fes:PropertyIsEqualTo>
    val response = new GetFeatureRequest("au:AdministrativeBoundary", filter).execute()
    (response must haveA200ResponseCode) and
      (response.value.getXml \\ "member" must haveSize(9))
  }
   
   def xPathAttributeFilter = {
    val filter = <fes:PropertyIsEqualTo>
              <fes:ValueReference>au:country/gmd:Country/@codeListValue</fes:ValueReference>
               <fes:Literal>FR</fes:Literal>
            </fes:PropertyIsEqualTo>
    val response = new GetFeatureRequest("au:AdministrativeBoundary", filter).execute()
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
      val response = GetWfsRequest("2.0.0", "GetFeature", "typeName" -> "au:AdministrativeBoundary", "featureid" -> 8269).execute()
      val xmlData = (response.value.getXml \\ "member")
    		
    (response must haveA200ResponseCode) and
      (xmlData \ "AdministrativeBoundary" must haveSize(1)) and
      (xmlData \\ "geometry" must haveSize(1)) and
      (xmlData \\ "inspireId" must haveSize(1)) and
      (xmlData \\ "inspireId" \ "Identifier" \ "localId" must haveSize(1)) and
      (xmlData \\ "inspireId" \ "Identifier" \ "namespace" must haveSize(1)) and
      (xmlData \\ "inspireId" \ "Identifier" \ "versionId" must haveSize(1)) and
      (xmlData \\ "nationalLevel" must haveSize(1)) and
      (xmlData \\ "legalStatus" must haveSize(1)) and
      (xmlData \\ "technicalStatus" must haveSize(1)) and
      (xmlData \\ "beginLifespanVersion" must haveSize(1)) and
      (xmlData \\ "endLifespanVersion" must haveSize(1)) and
      (xmlData \\ "admUnit" must haveSize(1)) and
      ((xmlData \\ "admUnit" \@ "xlink:href").head must contain("AdministrativeUnit"))
      ((xmlData \\ "admUnit" \@ "xlink:href").head must contain("FR2100000000"))
  } 
    
     def abResolve1 = {
      val response = GetWfsRequest("2.0.0", "GetFeature", "typeName" -> "au:AdministrativeBoundary",
          "resolve" -> "local", "resolveDepth" -> "1", "featureid" -> 8269).execute()
      val xmlData = (response.value.getXml \\ "member")
      
    (response must haveA200ResponseCode) and
    (xmlData \\ "admUnit" must haveSize(1)) and
    (xmlData \\ "admUnit" \ "AdministrativeUnit" must haveSize(1)) and
      ((xmlData \\ "admUnit" \ "AdministrativeUnit" \@ "gml:id").head must contain("FR2100000000"))
  } 
     
     def auNoResolve = {
      val response = GetWfsRequest("2.0.0", "GetFeature", "typeName" -> "au:AdministrativeUnit", "featureid" -> "FR2100000000").execute()
      val xmlData = (response.value.getXml \\ "member")
    		
    (response must haveA200ResponseCode) and
      (xmlData \ "AdministrativeUnit" must haveSize(1)) and
      (xmlData \\ "geometry" must haveSize(1)) and
      (xmlData \\ "inspireId" must haveSize(1)) and
      (xmlData \\ "inspireId" \ "Identifier" \ "localId" must haveSize(1)) and
      (xmlData \\ "inspireId" \ "Identifier" \ "namespace" must haveSize(1)) and
      (xmlData \\ "inspireId" \ "Identifier" \ "versionId" must haveSize(1)) and
      (xmlData \\ "nationalLevel" must haveSize(1)) and
      (xmlData \\ "nationalLevelName" must haveSize(1)) and
      (xmlData \\ "country" must haveSize(1)) and
      (xmlData \\ "name" must haveSize(1)) and
      (xmlData \\ "SpellingOfName" must haveSize(1)) and
      (xmlData \\ "residenceOfAuthority" must haveSize(1)) and
      (xmlData \\ "beginLifespanVersion" must haveSize(1)) and
      (xmlData \\ "endLifespanVersion" must haveSize(1)) and
      (xmlData \\ "NUTS" must haveSize(1)) and
      (xmlData \\ "condominium" must haveSize(1)) and
      (xmlData \\ "lowerLevelUnit" must haveSize(3)) and
      (((xmlData \\ "lowerLevelUnit")(0) \@ "xlink:href").head must contain("FR2300000000"))and
      (((xmlData \\ "lowerLevelUnit")(1) \@ "xlink:href").head must contain("FR5200000000"))and
      (((xmlData \\ "lowerLevelUnit")(2) \@ "xlink:href").head must contain("FR9100000000"))
  } 
 
  lazy val capabilities = {
    val response = GetWfsRequest("2.0.0", "GetCapabilities").execute()
    response.value.getXml
  }
  def hasFilterCapability = (spec:String) => {
    val constraintName = extract1(spec)
    val minTempFilter = (capabilities \\ "Filter_Capabilities" \ "Conformance" \ "Constraint") filter (e => (e @@ "name").head == constraintName)
    (minTempFilter.headOption must beSome) and
      ((minTempFilter \ "DefaultValue").text.toUpperCase() must_== "TRUE")
  }
}