package c2c.webspecs
package geoserver
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.Matcher
import org.specs2.execute.Result
import _root_.scala.xml.Text

/**
 * Ref : Inspire document 09-026r1_Filter_Encoding.pdf
 * Chapt : 7.4.4
 * Name : XPath Expressions
 **/

@RunWith(classOf[JUnitRunner])
class MinimumXPathSpec extends GeoserverSpecification {

  def isImpl = 
    "Minimum XPath tests".title ^
    	"Minimum XPath filter test suite supporting Inspire required tests" ^
    	"a. The abbreviated form of the child and attribute axis specifier shall be supported" ! basicXPathGrammar ^
    	"b. The context node shall be the resource element" ! contextNode ^
    	"c. Each step in the path may include an XPath predicate" ! basicXPathGrammar ^
    	"d. At least child grammar predicate expression items shall be supported" ! childIndexFilter
    	
    /**
     * a)
     * The abbreviated form of the child and attribute axis specifier (see W3C XML Path Language) shall be supported.
     */
    	
   def basicXPathGrammar =
   {
    val filterChild = <fes:PropertyIsEqualTo>
              <fes:ValueReference>au:country/gmd:Country</fes:ValueReference>
               <fes:Literal>FR</fes:Literal>
            </fes:PropertyIsEqualTo>
    
    val filterAttribute = <fes:PropertyIsEqualTo>
              <fes:ValueReference>au:country/gmd:Country/@codeListValue</fes:ValueReference>
               <fes:Literal>FR</fes:Literal>
            </fes:PropertyIsEqualTo>
      
    val responseChild = new GetFeatureRequest("au:AdministrativeBoundary", filterChild).execute()
    val responseAttribute = new GetFeatureRequest("au:AdministrativeBoundary", filterAttribute).execute()
    
    val expectedValue=9
    
    println(responseChild.value.getText.take(1000))
    println(responseAttribute.value.getText.take(1000))
    
    (responseChild must haveA200ResponseCode) and
      (responseChild.value.getXml \\ "member" must haveSize(expectedValue)) and
      (responseAttribute must haveA200ResponseCode) and
      (responseAttribute.value.getXml \\ "member" must haveSize(expectedValue))
  }
  
  /**
     * b)
     * The context node shall be the resource element, except in the case of a join operation, 
     * in which case the context node shall be the parent of the resource element.
     */
    	
   def contextNode =
   {
    val filter = <fes:PropertyIsEqualTo>
              <fes:ValueReference>/au:nationalLevel</fes:ValueReference>
               <fes:Literal>2ndOrder</fes:Literal>
            </fes:PropertyIsEqualTo>
      
    val filterContext = <fes:PropertyIsEqualTo>
              <fes:ValueReference>au:nationalLevel</fes:ValueReference>
               <fes:Literal>2ndOrder</fes:Literal>
            </fes:PropertyIsEqualTo>
      
    val response = new GetFeatureRequest("au:AdministrativeUnit", filter).execute()
    val responseContext = new GetFeatureRequest("au:AdministrativeUnit", filterContext).execute()
    
    val expectedValue=10
    
    println(response.value.getText.take(1000))
    println(responseContext.value.getText.take(1000))
    
    (response must haveA200ResponseCode) and
      (response.value.getXml \\ "member" must haveSize(expectedValue)) and
      (responseContext must haveA200ResponseCode) and
      (responseContext.value.getXml \\ "member" must haveSize(expectedValue))
  }
   
    /**
     * d) 1.
     * a positive non-zero integer may be used to indicate which child of the context node should be selected	
     */
    	
   def childIndexFilter =
   {
    val filter =  <fes:PropertyIsLike wildCard="*" singleChar="." escapeChar="!">
             <fes:ValueReference>gml:name[1]</fes:ValueReference>
               <fes:Literal>*1</fes:Literal>
            </fes:PropertyIsLike>
    
    val filter2 =  <fes:PropertyIsLike wildCard="*" singleChar="." escapeChar="!">
             <fes:ValueReference>gml:name[2]</fes:ValueReference>
               <fes:Literal>*1</fes:Literal>
            </fes:PropertyIsLike>
        
    val response = new GetFeatureRequest("gsmlgu:GeologicUnit", filter).execute()
    val response2 = new GetFeatureRequest("gsmlgu:GeologicUnit", filter2).execute()
    
    println(response.value.getText.take(1000))
    println(response2.value.getText.take(1000))
    
    (response must haveA200ResponseCode) and
      (response.value.getXml \\ "member" must haveSize(1)) and
      (response2 must haveA200ResponseCode) and
      (response2.value.getXml \\ "member" must haveSize(0))
  }

}