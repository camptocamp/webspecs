package c2c.webspecs
package geonetwork
package geocat
package spec.WP6

import csw._
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.ImportStyleSheets.NONE
import c2c.webspecs.geonetwork._
import c2c.webspecs.{XmlValue, Response, IdValue, GetRequest}
import accumulating._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import scala.xml.NodeSeq
import OutputSchemas._
import c2c.webspecs.geonetwork.csw.CswGetCapabilities
import c2c.webspecs.GetRequest


@RunWith(classOf[JUnitRunner]) 
class CswOutputSchemaSpec extends GeocatSpecification(UserProfiles.Editor) {  def is =
	"CSW output schemas".title ^ Step(setup) ^
	"This specification tests the usage of different output schemas through CSW" ^
		"Test the CSW server via GetCapabilities request" ^ testGetCapabilities.toGiven ^
			"must have ${http://www.opengis.net/cat/csw/2.0.2} as an outputSchema"			  ^ outputSchema.toThen ^
			"must have ${http://www.isotc211.org/2005/gmd} as an outputSchema"			      ^ outputSchema.toThen ^
			"must have ${http://www.geocat.ch/2008/che} as an outputSchema"			          ^ outputSchema.toThen ^
			"must have ${http://www.isotc211.org/2008/gm03_2} as an outputSchema"			  ^ outputSchema.toThen ^
																								endp ^
		"Import a metadata" ^ Step(importMetadataId) ^
		"Getting the metadata previously inserted in dublin-core output"   ! testDublinCore   ^
		"Getting the metadata previously inserted in iso19139 output"      ! testiso19139     ^
		"Getting the metadata previously inserted in iso19139.che output"  ! testiso19139che  ^
//		"Getting the metadata previously inserted in GM03 output"          ! testGM03         ^
//		"Getting the metadata previously inserted in its own format"     ! testDublinCore ^
		"Delete the inserted metadata"										^ Step(deleteMetadata)  ^
																			end ^ Step(tearDown)
		
	
	val testGetCapabilities = () =>  CswGetCapabilities().execute().value.getXml
	
	val outputSchema = (capabilities : NodeSeq, descriptor : String) => {
	  val schema = extract1(descriptor)
	  val getRecordsOperations = capabilities \\ "Operation" filter { _ @@ "name" == List("GetRecords") }
	  val getOutputSchemas = (getRecordsOperations \\ "Parameter" filter { _ @@ "name" == List("outputSchema") }) \\ "Value"

	  getOutputSchemas.map{_.text} must contain(schema)
	}
  def testDublinCore =  {
	  // how to get the fileId of the inserted MD ?
	  val getRecordResult = CswGetRecordById(importMetadataId,  new OutputSchema("http://www.opengis.net/cat/csw/2.0.2"){}).execute()
	  (getRecordResult.value.getXml \\ "title").head.prefix must_== "dc"
  }
  def testiso19139 = {
	  val getRecordResult = CswGetRecordById(importMetadataId,  new OutputSchema("http://www.isotc211.org/2005/gmd"){}).execute()
	  (getRecordResult.value.getXml \\ "MD_Metadata").head.prefix must_== "gmd"

  }
  def testiso19139che = {
	  val getRecordResult = CswGetRecordById(importMetadataId,  OutputSchemas.CheIsoRecord).execute()
	  (getRecordResult.value.getXml \\ "CHE_MD_Metadata").head.prefix must_== "che"
  }
  def testGM03 = {
	  val getRecordResult = CswGetRecordById(importMetadataId,  OutputSchemas.GM03Record).execute()
	  (getRecordResult.value.getXml \\ "GM03_2Comprehensive.Comprehensive") must not beEmpty
  }
  
  lazy val importMetadataId = {
    val (_,importMd) = ImportMetadata.defaults(uuid, "/geocat/data/metadata.iso19139.che.xml",true, getClass)
    
    val md = (importMd then GetRawMetadataXml).execute(ImportStyleSheets.NONE).value.getXml
    val response = (md \\ "fileIdentifier").text.trim
    response
  }
    def deleteMetadata = {
		  GetRequest("metadata.delete", ("uuid" -> importMetadataId)).execute()
    }
}