package c2c.webspecs
package geonetwork
package geocat
package spec.WP16

import scala.xml.NodeSeq
import org.apache.http.entity.mime.content.StringBody
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._


@RunWith(classOf[JUnitRunner]) 
class MetadataValidationReportSpec extends GeocatSpecification(UserProfiles.Admin) {  def is =
	"metadata.validation.report test".title 														 ^ Step(setup)                       ^
	"Loads a valid sample metadata"      	 	              									     ^ Step(importValidMetadataId)       ^
	"Loads an invalid sample metadata"      	 	              									 ^ Step(importInvalidMetadataId)     ^
	"Logs in as administrator (note : should the service be available to editors too ?)"      	 	 ^ Step(config.adminLogin.execute())         ^
	"Tests the ${valid} inserted metadata against the metadata.validation.report webservice"		 ! callValidationReport              ^
	"Tests the ${invalid} inserted metadata against the metadata.validation.report webservice"		 ! callValidationReport              ^
	"Delete the inserted metadatas"																	 ^ Step(deleteMetadatas)             ^
																								       end ^ Step(tearDown)	
		
																								       
  def callValidationReport = (desc:String) => {
	  	  val valid = if (extract1(desc) == "valid") true else false
		  def serviceCall = if (valid) GetRequest("metadata.validation.report", ("id" -> importValidMetadataId)).execute() else
		                       GetRequest("metadata.validation.report", ("id" -> importInvalidMetadataId)).execute()
		                       
		  serviceCall must haveA200ResponseCode
		  
		  val errorsFound = serviceCall.value.getXml \\ "errorFound"
		  
		  println(serviceCall.value.getXml)
		  if (valid) errorsFound must beEmpty else errorsFound.length must be_>=(1)
		  
}														    
  lazy val importValidMetadataId = {
       val importMdRequest = ImportMetadata.defaults(uuid, "/geocat/data/metadata.iso19139.che.xml",true, getClass)._2
       val md = (importMdRequest then GetRawMetadataXml).execute().value.getXml
       val response = (md \\ "fileIdentifier").text.trim
       response
  	}
  lazy val importInvalidMetadataId = {
    // TODO : setting true in the following line reveals a bug in the mef.import service
       val importMdRequest = ImportMetadata.defaults(uuid, "/geocat/data/metadata.iso19139.che.invalid.xml",false, getClass)._2
       val md = (importMdRequest then GetRawMetadataXml).execute().value.getXml
       val response = (md \\ "fileIdentifier").text.trim
       response
  	}
  
    def deleteMetadatas = {
		  GetRequest("metadata.delete", ("uuid" -> importValidMetadataId)).execute()
		  GetRequest("metadata.delete", ("uuid" -> importInvalidMetadataId)).execute()
    }
			
}