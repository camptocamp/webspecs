package c2c.webspecs
package geonetwork
package geocat
package spec.WP6

import org.junit.runner.RunWith
import scala.xml._
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step

import c2c.webspecs.geonetwork.UserProfiles


@RunWith(classOf[JUnitRunner]) 
class CswXmlTestSpec extends GeocatSpecification {  def is =
	"GeoNetwork-trunk XML testsuite for CSW server".title 							        ^ Step(setup)                     ^
	  "Loads a sample metadata"      	 	                			  	                ^ Step(importMetadataId)          ^
    	"Process test using XML file : ${csw-DescribeRecordWithMD_Metadata}"                ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-DescribeRecordWithMultipleTypeName}"           ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-DescribeRecord}"                               ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetCapabilitiesSections}"                      ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetCapabilities}"                              ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetDomainParameterName}"                       ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetDomainPropertyName}"                        ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordByIdFraIsoRecord}"                    ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordByIdIsoRecord}"                       ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordById}"                                ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsCQLAny}"                             ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsCQLEquals}"                          ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsElementName}"                        ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsFilterGeoBbox2Equals}"               ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsFilterGeoBboxEquals}"                ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsFilterGeoBboxForMoreThan180degrees}" ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsFilterGeoBoxIntersects}"             ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsFilterGeoBox}"       				! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsFilterGeoEnvelope}"        			! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsFilterIsEqualToOnKeyword}"        	! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsFilterIsEqualToOnProtocol}"        	! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsFilterIsEqualToPhraseOnTitle}"       ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsFilterIsLikeOnAny}"        			! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsFilterIsLikePhraseOnAbstract}"       ! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsFilterRangeCswIsoRecord}"        	! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsFilterService}"        				! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsNoFilterCswIsoRecord}"        		! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsNoFilterFraIsoRecord}"        		! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsNoFilterIsoRecord}"        			! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsNoFilterResultsWithSummary}"        	! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsNoFilterResults}"        			! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsNoFilterValidate}"        			! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsNoFilter}"        					! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-GetRecordsSortBy}"        						! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-Harvest}"        								! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-OwsException}"        							! ProceedXmlTest		          ^
	  "Delete the inserted metadata"									                    ^ Step(deleteMetadata)            ^
																		 			        end ^ Step(tearDown)
			
  lazy val importMetadataId = {
    val importMdRequest = ImportMetadata.defaults(uuid, "/geocat/data/metadata.iso19139.che.xml",false, getClass)._2
	val md = (importMdRequest then GetRawMetadataXml).execute().value.getXml
	val response = (md \\ "fileIdentifier").text.trim
	response
  }

    def deleteMetadata = {
		  GetRequest("metadata.delete", ("uuid" -> importMetadataId)).execute()

    }

  def ProceedXmlTest = (desc:String) => {
    val xmlFile = extract1(desc) + ".xml"
    val (xmlResource,_) = ResourceLoader.loadDataFromClassPath("/geonetwork/data/cswXmlTests/"+xmlFile, getClass, uuid)
    val cswTestRequest = XmlPostRequest("csw", XML.loadString(xmlResource)).execute()
    
    // In some cases (a failure is expected), we do not want to trigger errors by parsing XML
    if (! xmlFile.contains("FraIsoRecord") && (! xmlFile.contains("GetRecordsFilterGeoBbox2Equals")) && (! xmlFile.contains("OwsException")))
      cswTestRequest.value.getXml

    (cswTestRequest must haveA200ResponseCode)
    
    // checking failing responses
    // TODO: how to avoid XML parsing but test that an error is effectively returned ?
    
    //    if (xmlFile.contains("GetRecordsFilterGeoBbox2Equals"))
    //    	cswTestRequest.basicValue.toTextValue.text must contain("Error when parsing spatial filter")
    //    if (xmlFile.contains("OwsException"))
    //    	cswTestRequest.basicValue.toTextValue.text must contain("Error when parsing spatial filter")
  }

}
