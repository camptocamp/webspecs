package c2c.webspecs
package geonetwork
package geocat
package spec.WP16

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import c2c.webspecs.geonetwork.edit.StartEditingHtml
import c2c.webspecs.geonetwork.edit.UpdateMetadata


@RunWith(classOf[JUnitRunner]) 
class MetadataValidationReportSpec extends GeocatSpecification {  def is =
	"metadata.validation.report test".title 														 ^ Step(setup)                       ^
	"Loads a valid sample metadata"      	 	              									     ^ Step(importValidMetadataId)       ^
	"Loads an invalid sample metadata"      	 	              									 ^ Step(importInvalidMetadataId)     ^
	"Tests the ${valid} inserted metadata against the metadata.validation.report webservice"		 ! callValidationReport              ^
	"Tests the ${invalid} inserted metadata against the metadata.validation.report webservice"		 ! callValidationReport              ^
																								       end ^ Step(tearDown)	
		
																								       
  def callValidationReport = (desc:String) => {
    val valid = if (extract1(desc) == "valid") true else false

    val id = if (valid) importValidMetadataId else importInvalidMetadataId

    // Need to start edit to put metadata in context
    val editValue = StartEditingHtml().execute(Id(id)).value
    // since the invalid metadata was not validated on import we need to do something to trigger a validation so that it is 
    // validated.  As such we update it with no changes
    UpdateMetadata(finish = false, showValidationErrors = true).execute(editValue)
    val serviceCall = GetRequest("metadata.validate!", "id" -> id).execute()

    serviceCall must haveA200ResponseCode

    val errorsFound = serviceCall.value.getXml \\ "response" \\ "_" filter {
      n => n.label == "failed-assert" || n.label == "error"
    }

    if (valid) errorsFound must beEmpty else errorsFound.length must be_>=(1)

  }														    
  lazy val importValidMetadataId = {
       val importMdRequest = ImportMetadata.defaults(uuid, "/geocat/data/metadata.iso19139.che.xml",true, getClass)._2
       val id = importMdRequest.execute().value.id
       registerNewMd(Id(id))
       id
  	}
  lazy val importInvalidMetadataId = {
    
    // TODO : setting true in the following line reveals a bug in the mef.import service
       val importMdRequest = ImportMetadata.defaults(uuid, "/geocat/data/metadata.iso19139.che.invalid.xml",false, getClass)._2
       val id = importMdRequest.execute().value.id
       registerNewMd(Id(id))
       id
  	}
  
			
}