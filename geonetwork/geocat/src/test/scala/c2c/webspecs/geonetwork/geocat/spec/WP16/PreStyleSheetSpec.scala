package c2c.webspecs
package geonetwork
package geocat
package spec.WP16

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._

@RunWith(classOf[JUnitRunner]) 
class PreStyleSheetSpec extends GeocatSpecification {  def is =
	"Test of the pre-rendering with multiple XSL stylesheet feature".title 							 ^ Step(setup)                       ^
	"Loads a sample metadata"      	 	              									  		     ^ Step(importMetadataId)            ^
	"Tests the inserted metadata against the metadata.show.embedded webservice"		                 ! callMetadataShowEmbeddedService   ^
	"Delete the inserted metadata"																	 ^ Step(deleteMetadata)              ^
																								       end ^ Step(tearDown)	
		
																								       
  def callMetadataShowEmbeddedService =  {
		  def serviceCall = GetRequest("metadata.show.embedded", ("uuid" -> importMetadataId)).execute() 
		                  
		  serviceCall must haveA200ResponseCode
		  
		  val result = serviceCall.value.getXml
		  result.head.label must_== "html" and ((result \\ "table").length must be_>=(0))
  }

  lazy val importMetadataId = {
       val importMdRequest = ImportMetadata.defaults(uuid, "/geocat/data/metadata.iso19139.che.xml",false, getClass)._2
       val md = (importMdRequest then GetRawMetadataXml).execute().value.getXml
       val response = (md \\ "fileIdentifier").text.trim
       response
  	}
 
    def deleteMetadata = {
		  GetRequest("metadata.delete", ("uuid" -> importMetadataId)).execute()
    }
			
}