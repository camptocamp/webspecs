package c2c.webspecs
package geonetwork
package geocat
package spec.WP12

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step

import c2c.webspecs.GetRequest
import c2c.webspecs.geonetwork.UserProfiles
import c2c.webspecs.geonetwork.ImportMetadata
import c2c.webspecs.geonetwork.GetRawMetadataXml


@RunWith(classOf[JUnitRunner]) 
class GM03V1Spec extends GeocatSpecification {
	def is = {
	  "GM03v1 Import - Export test".title 	                                                          ^ Step(setup) ^
	  	"Imports a GM03 v1 metadata and converts it into iso19139.che for storage into the catalogue" ^ Step(importMetadataId) ^
	  	"Gets the previously inserted MD as GM03v2"                                                   ! getAsGm03v2 ^
	  	"Gets the previously inserted MD as GM03v2small"                                              ! getAsGm03v2small ^
	  	"Delete the inserted metadata"							                                      ^ Step(deleteMetadata)  ^
																                                      end ^ Step(tearDown)														
	}
	
	lazy val importMetadataId = {
		val (_,importMd) = ImportMetadata.defaults(uuid, "/geocat/data/metadata.gm03_V1.xml",false, getClass, GeocatImportStyleSheets.GM03_V1)
		val md = (importMd then GetRawMetadataXml).execute().value.getXml
		val response = (md \\ "fileIdentifier").text.trim
		response
	}
	def deleteMetadata = {
			GetRequest("metadata.delete", ("uuid" -> importMetadataId)).execute()
	}
	
		def getAsGm03v2 = {
		val response = GetRequest("gm03.xml", ("uuid" -> importMetadataId)).execute()
		(response.value.getXml \\  "GM03_2Core.Core.MD_Metadata" \ "fileIdentifier").text.trim must beEqualTo (importMetadataId)
	}

	def getAsGm03v2small = {
		val response = GetRequest("gm03small.xml", ("uuid" -> importMetadataId)).execute()
		(response.value.getXml \\ "fileIdentifier").text.trim must beEqualTo (importMetadataId)
	}
}