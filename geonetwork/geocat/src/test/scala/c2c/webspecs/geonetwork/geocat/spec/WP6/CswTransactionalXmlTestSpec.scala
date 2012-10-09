package c2c.webspecs
package geonetwork
package geocat
package spec.WP6

import scala.xml._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step

import c2c.webspecs.geonetwork.UserProfiles


@RunWith(classOf[JUnitRunner]) 
class CswTransactionalXmlTestSpec extends GeocatSpecification {  def is =
	"GeoNetwork-trunk XML testsuite for CSW server (transactional)".title 					^ Step(setup)                     ^
	  "Loads a sample metadata"      	 	                			  	                ^ Step(importMetadataId)          ^
	  "Login as admin"																		^ Step(config.adminLogin.execute())       ^
  	    "Process test using XML file : ${csw-TransactionDelete}"        					! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-TransactionInsert}"        					! ProceedXmlTest		          ^
  	    "Process test using XML file : ${csw-TransactionUpdate}"        					! ProceedXmlTest		          ^
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

    (cswTestRequest must haveA200ResponseCode)
  }

}
