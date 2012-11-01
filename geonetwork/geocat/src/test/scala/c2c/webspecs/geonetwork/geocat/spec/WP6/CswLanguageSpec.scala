package c2c.webspecs
package geonetwork
package geocat
package spec.WP6

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step

import c2c.webspecs.geonetwork.csw._
import c2c.webspecs.GetRequest
import c2c.webspecs.geonetwork.UserProfiles
import c2c.webspecs.geonetwork.ImportMetadata
import c2c.webspecs.geonetwork.GetRawMetadataXml


@RunWith(classOf[JUnitRunner]) 
class CswLanguageSpec extends GeocatSpecification {

  def is = {
	  "CSW service by language".title 	                                                                                                           ^ Step(setup) ^
	  	"Imports a metadata, and test it against different locales, the non-multilingual responses (dublin-core) should be in the french language" ^ Step(importMetadataId) ^
	  	   "Testing the ${fre} CSW service, getting previously inserted MD in its french version (${FR}, ${GetRecordById})"                        ! CswGet ^
	  	   "Testing the ${ger} CSW service, getting previously inserted MD in its german version (${DE}, ${GetRecordById})"                        ! CswGet ^
   	  	   "Testing the ${eng} CSW service, getting previously inserted MD in its english version (${EN}, ${GetRecordById})"		               ! CswGet ^
   	  	   "Testing the metadata using CSW service on ${ita} locale -> fallback on default MD locale (${ITA}, ${GetRecordById})"                   ! CswGet ^
   	  	   "Testing the ${fre} CSW service, getting previously inserted MD in its french version (${FR}, ${GetRecords})"                           ! CswGet ^
	  	   "Testing the ${ger} CSW service, getting previously inserted MD in its german version (${DE}, ${GetRecords})"                           ! CswGet ^
   	  	   "Testing the ${eng} CSW service, getting previously inserted MD in its english version (${EN}, ${GetRecords})"                          ! CswGet ^
   	  	   "Testing the metadata using CSW service on ${ita} locale -> fallback on default MD locale (${ITA}, ${GetRecords})"                      ! CswGet ^
		"Delete the inserted metadata"							                                                                                   ^ Step(deleteMetadata)  ^
																                                                                                     end ^ Step(tearDown)														
  }

  lazy val importMetadataId = {
    val (_, importMd) = ImportMetadata.defaults(uuid, "/geocat/data/metadata.iso19139.che.xml", false, getClass)

    val md = (importMd then GetRawMetadataXml).execute().value.getXml
    val response = (md \\ "fileIdentifier").text.trim
    response
  }
  def deleteMetadata = {
    GetRequest("metadata.delete", ("uuid" -> importMetadataId)).execute()
  }
  def CswGet = (description: String) => {
    val (languageCode, expectedLang, cswService) = extract3(description)

    val CswRequest = if (cswService == "GetRecordById")
      CswGetRecordById(importMetadataId,
        outputSchema = OutputSchemas.Record,
        url = languageCode + "/csw",
        resultType = ResultTypes.results)

    else
      CswGetRecordsRequest(PropertyIsEqualTo("Identifier", importMetadataId).xml,
        maxRecords = 1,
        resultType = ResultTypes.results,
        outputSchema = OutputSchemas.Record,
        url = languageCode + "/csw")

    val title = (CswRequest.execute().value.getXml \\ "title").text.trim.toUpperCase

    if (languageCode != "ita")
      title must_== (expectedLang + " TITLE")
    else
      title must_== ("FR TITLE")
  }
	
	
}