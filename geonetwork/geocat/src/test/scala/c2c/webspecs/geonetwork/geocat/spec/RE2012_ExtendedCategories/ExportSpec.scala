package c2c.webspecs
package geonetwork
package geocat
package spec.RE2012_ExtendedCategories

import csw._
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExportSpec  extends GeocatSpecification {  def is = 
	
  "This specification tests the export of Extended Categories"                  ^ Step(setup)               ^
    "Inserting a CHE metadata with extended categories"                        ^ Step(importAndGetMetadataId) ^
    "${getRecordById} shouldn't have specialized code"        ! correctIso19139Export   ^
    "${iso19139 export} should have specialized code"        ! correctIso19139Export   ^
    "GM03 export both codes"        ! correctGM03Export   

  /**
   * Load a MD with one category 'elevation' and one extended category "geoscientificInformation_Geology"
   * The MD also contain main category "geoscientificInformation"
   * 
   * iso19139  export should just contain main category 'elevation' and "geoscientificInformation"
   * GM03 export should contains both codes "geoscientificInformation" & "geoscientificInformation_Geology"
   */
    
  lazy val importAndGetMetadataId = {
      val id = importMd(1,"/geocat/data/metadata.iso19139.che-ext-cat.xml",uuid.toString).head
      (GetRawMetadataXml.execute(id).value.getXml \\ "fileIdentifier").text.trim
  }
  
  def correctIso19139Export = (s:String) => {
    
		  def getRecordById = extract1(s) match {
	      case "getRecordById" =>
	         CswGetRecordById(importAndGetMetadataId,  outputSchema = OutputSchemas.IsoRecord).execute()
	      case "iso19139 export" =>
	         GetRequest("xml_iso19139", ("uuid" -> importAndGetMetadataId)).execute()
	      case "GM03 export" =>
	         GetRequest("gm03.xml", ("uuid" -> importAndGetMetadataId)).execute()
	
	    }		                  
		getRecordById must haveA200ResponseCode
		  
		  val response = getRecordById.value.getXml
		  
		  (response \\ "topicCategory" must haveSize(2)) and
    	   ((response \\ "topicCategory"  \ "MD_TopicCategoryCode").text.trim must contain("geoscientificInformation")) and
    		((response \\ "topicCategory"  \ "MD_TopicCategoryCode").text.trim must not contain("geoscientificInformation_Geology")) 
  }
  
  def correctGM03Export = {
    
	     val exportGM03 = GetRequest("gm03.xml", ("uuid" -> importAndGetMetadataId)).execute()
	
		exportGM03 must haveA200ResponseCode
		  
		 val response = exportGM03.value.getXml
		  
		  (response \\ "topicCategory" \ "GM03_2Core.Core.MD_TopicCategoryCode_" must haveSize(3)) and
    	   ((response \\ "GM03_2Core.Core.MD_TopicCategoryCode_"  \ "value").text.trim must contain("geoscientificInformation")) and
    		((response \\ "GM03_2Core.Core.MD_TopicCategoryCode_"  \ "value").text.trim must contain("geoscientificInformation_Geology")) 
  }
  
}