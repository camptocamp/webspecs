package c2c.webspecs
package geonetwork
package geocat
package spec.WP6

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step

import c2c.webspecs.geonetwork.ImportStyleSheets.NONE
import c2c.webspecs.geonetwork.csw._
import c2c.webspecs.GetRequest
import c2c.webspecs.geonetwork.UserProfiles
import c2c.webspecs.geonetwork.ImportMetadata
import c2c.webspecs.ResourceLoader
import c2c.webspecs.geonetwork.GetRawMetadataXml


@RunWith(classOf[JUnitRunner]) 
class CswLanguageSpec extends GeocatSpecification(UserProfiles.Editor) {
	def is = {
	  "CSW service by language".title 	^ Step(setup) ^
	  	"Importing a metadata" 			^ Step(importMetadataId) ^
	  	   "Getting the metadata using the ${fra} CSW service (${FR}, ${GetRecordById})" ! CswGet ^
	  	   "Getting the metadata using the ${deu} CSW service (${DE}, ${GetRecordById})" ! CswGet ^
   	  	   "Getting the metadata using the ${eng} CSW service (${EN}, ${GetRecordById})" ! CswGet ^
   	  	   "Getting the metadata using the ${fra} CSW service (${FR}, ${GetRecords})" ! CswGet ^
	  	   "Getting the metadata using the ${deu} CSW service (${DE}, ${GetRecords})" ! CswGet ^
   	  	   "Getting the metadata using the ${eng} CSW service (${EN}, ${GetRecords})" ! CswGet ^
		"Delete the inserted metadata"							^ Step(deleteMetadata)  ^
																end ^ Step(tearDown)
																
	}
	
	lazy val importMetadataId = {
				val (_,importMd) = ImportMetadata.defaults(uuid, "/geocat/data/metadata.iso19139.che.xml",true, getClass)
	
				val md = (importMd then GetRawMetadataXml)(NONE).value.getXml
				val response = (md \\ "fileIdentifier").text.trim
					response
	}
	def deleteMetadata = {
			GetRequest("metadata.delete", ("uuid" -> importMetadataId))(Nil)
	}
	def CswGet = (description : String) => {
	  val (languageCode, expectedLang, cswService) = extract3(description)
	  
	  val CswRequest =  if (cswService == "GetRecordById")
							  CswGetByFileId(importMetadataId,
								  						 outputSchema = OutputSchemas.Record,
								  						 url= "http://" + Properties.testServer + "/geonetwork/srv/"+languageCode+"/csw", 
								  									resultType = ResultTypes.results)
					
						else 
							  CswGetRecordsRequest(PropertyIsEqualTo("Identifier", importMetadataId).xml,
									  			   maxRecords = 1,
									  			   resultType = ResultTypes.results,
									  			   outputSchema = OutputSchemas.Record,
									  			   url= "http://" + Properties.testServer + "/geonetwork/srv/"+languageCode+"/csw")
	  									
			  									
	  val title = (CswRequest(Nil).value.getXml \\ "title").text.trim.toUpperCase		  									

      
      title must_== (expectedLang + " TITLE")

	}
	
	
}