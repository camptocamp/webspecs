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
class ImportCheMetadataSpec  extends GeocatSpecification {  def is = 
	
  "This specification tests using the iso19139.CHE schema"                  ^ Step(setup)               ^
    "Inserting a CHE metadata with extended categories"                        ^ Step(importAndGetMetadata) ^
    "Should suceed with a 200 response"                                     ! {importAndGetMetadata must haveA200ResponseCode}   ^
    "GetRecordById iso19139 shouldn't have extended category"        ! correctGetRecordById   ^
    "export in iso19139 shouldn't have not extended category"        ! correctGetRecordById   ^
    "export in GM03 shouldn't not have extended category"        ! correctGetRecordById   

  /**
   * Load a MD with one category 'elevation' and one extended category "geoscientificInformation_Geology"
   */
  lazy val importAndGetMetadata = {
      val id = importMd(1,"/geocat/data/metadata.iso19139.che.xml",uuid.toString).head
      val fileId = (GetRawMetadataXml.execute(id).value.getXml \\ "fileIdentifier").text.trim
      CswGetRecordById(fileId,  outputSchema = OutputSchemas.IsoRecord).execute()
  }
  lazy val metadataXml = importAndGetMetadata.value.getXml

  def correctGetRecordById = {
    (metadataXml \\ "topicCategory" must haveSize(2)) and
    	(metadataXml \\ "topicCategory"  \ "MD_TopicCategoryCode" must contain("geoscientificInformation")) and
    		(metadataXml \\ "topicCategory"  \ "MD_TopicCategoryCode" must contain("geoscientificInformation_Geology")) 
  }
}