package c2c.webspecs.geonetwork.geocat.spec.WP6

import c2c.webspecs.geonetwork.GeonetworkSpecification
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
class CswLanguageSpec extends GeonetworkSpecification(UserProfiles.Editor) {
	def is = {
	  "CSW service by language".title 	^ Step(setup) ^
	  	"Importing a metadata" 			^ Step(importMetadataId) ^
	  	   "Getting the metadata using the ${fra} CSW service (${FR})" ! CswGet ^
	  	   "Getting the metadata using the ${deu} CSW service (${DE})" ! CswGet ^
   	  	   "Getting the metadata using the ${eng} CSW service (${EN})" ! CswGet ^
		"Delete the inserted metadata"							^ Step(deleteMetadata)  ^
																end ^ Step(tearDown)
																
	}
	
	lazy val importMetadataId = {
		val name = "metadata.iso19139.che.xml"
				val (_,content) = ResourceLoader.loadDataFromClassPath("/geocat/data/"+name, getClass, uuid)
				val ImportMd = ImportMetadata.findGroupId(content,NONE,true)
	
				val md = (ImportMd then GetRawMetadataXml)(NONE).value.getXml
				val response = (md \\ "fileIdentifier").text.trim
					response
	}
	def deleteMetadata = {
			GetRequest("metadata.delete", ("uuid" -> importMetadataId))(Nil)
	}
	def CswGet(description : String) = {
	  val (languageCode, expectedLang) = extract2(description)
	  
	  val CswRequest = CswGetByFileId(importMetadataId,
			  						 outputSchema = OutputSchemas.Record,
			  						 url=Properties.testServer + "/geonetwork/srv/fra/csw", 
			  									resultType = csw.ResultTypes.results)
      
	  val title = (CswRequest(Nil).value.getXml \\ "title").text.trim.toUpperCase
      
      title must_== (expectedLang + " TITLE")

	}
	
	
}