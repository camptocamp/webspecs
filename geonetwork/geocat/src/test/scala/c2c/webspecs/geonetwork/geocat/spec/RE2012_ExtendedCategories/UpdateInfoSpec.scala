package c2c.webspecs
package geonetwork
package geocat
package spec.RE2012_ExtendedCategories

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._

@RunWith(classOf[JUnitRunner]) 
class UpdateInfoSpec extends GeocatSpecification {  def is =
	 "This specification tests update-info.xsl on Extended categories"               ^ Step(setup)               ^
    "Inserting a CHE metadata with Extended Cateogries"                   ^ Step(importAndGetMetadata) ^
    "Should suceed with a 200 response"                                     ! {importAndGetMetadata must haveA200ResponseCode}   ^
    "The new Metadata should contain the parent category"        ! correctMetadataWasRetrieved      ^
                                                                              Step(tearDown)

  /**
   * Load an MD with one simple category and one extended. The parent of the extended one is not present, like it would be
   * after editing and before updating the md.
   * In order to test update-info.xml is adding the parent properly.
   * 
   * <gmd:topicCategory>
        <gmd:MD_TopicCategoryCode>elevation</gmd:MD_TopicCategoryCode>
      </gmd:topicCategory>
       <gmd:topicCategory>
        <gmd:MD_TopicCategoryCode>geoscientificInformation_Geology</gmd:MD_TopicCategoryCode>
      </gmd:topicCategory>																								       
   */
  lazy val importAndGetMetadata = {
    val mdId = importMd(1, "/geocat/data/metadata.iso19139.che-ext-cat-edit.xml", uuid.toString).head
    GetRawMetadataXml.execute(mdId)
  }
  lazy val metadataXml = importAndGetMetadata.value.getXml
  
  def correctMetadataWasRetrieved = {
    println(metadataXml)
    	(metadataXml \\ "topicCategory" must haveSize(3)) and
    	((metadataXml \\ "topicCategory"  \ "MD_TopicCategoryCode").text.trim must contain("elevation")) and
    	 ((metadataXml \\ "topicCategory"  \ "MD_TopicCategoryCode").text.trim must contain("geoscientificInformation")) and
    	  ((metadataXml \\ "topicCategory"  \ "MD_TopicCategoryCode").text.trim must contain("geoscientificInformation_Geology")) 
  }
}