package c2c.webspecs
package geonetwork
package geocat
package spec.WP5

import csw._
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.ImportStyleSheets.NONE
import c2c.webspecs.geonetwork._
import c2c.webspecs.{XmlValue, Response, IdValue, GetRequest}
import accumulating._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import scala.xml.transform.BasicTransformer
import c2c.webspecs.geonetwork.geocat.spec.WP3.ProcessImportedMetadataSpec
import scala.xml.Node
import scala.xml.XML
import scala.xml.Elem
import org.specs2.execute.Result
import java.util.Date


@RunWith(classOf[JUnitRunner]) 
class SpatialSearchSpec extends SearchSpecification {  def is =
  "Spatial search queries".title ^
  "This specification tests how spatial search queries"             					          													 					^ Step(setup)               ^
      "First import several metadata that are to be searched for" 								  													 					^ Step(importedMetadataId)  ^
      "A spatial search for ${kantone:geneva} as a ${within} search should return the ${FR} md "	        				! basicSearch  ^
                                                                                                  													   		 Step(tearDown)


  def basicSearch(implicit maxRecords:Int = 10000, similarity:Double = 1,lang:String = "fra") = (s: String) => {
    val (areas, field, expectedMetadata) = extract3(s)
    
    val areaCodes = areas.toLowerCase match {
      case "kantone:geneva" => "kantone:25"
    }
    val similarityProperty = PropertyIsEqualTo("similarity", similarity.toString)
    val filter = similarityProperty and PropertyIsEqualTo(field, areaCodes)
    
    val xmlResponse = CswGetRecordsRequest(filter.xml, 
    									   resultType=ResultTypes.resultsWithSummary, 
    									   outputSchema = OutputSchemas.Record, 
    									   maxRecords = maxRecords,
    									   url = lang+"/csw")().value.getXml
    
    find(xmlResponse, expectedMetadata)
  }
  
  def currentLanguageFirst = {
    pending
  }
}