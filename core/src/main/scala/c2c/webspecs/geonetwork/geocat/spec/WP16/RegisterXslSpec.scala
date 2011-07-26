package c2c.webspecs
package geonetwork
package geocat
package spec.WP16

import scala.xml.NodeSeq
import org.apache.http.entity.mime.content.StringBody
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.ImportStyleSheets.NONE
import c2c.webspecs.geonetwork.csw.OutputSchemas._
import c2c.webspecs.geonetwork._
import c2c.webspecs.GetRequest
import c2c.webspecs.GetRequest
import c2c.webspecs.Response
import c2c.webspecs.XmlValue
import c2c.webspecs.GetRequest


@RunWith(classOf[JUnitRunner]) 
class RegisterXslSpec extends GeocatSpecification(UserProfiles.Admin) {  def is =
	"Xsl custom metadata XML output".title 															 ^ Step(setup) ^
			"Login as admin"																		 ^ Step(config.adminLogin()) ^
			"must load the XSL stylesheet via the the REST API (${bs_extended_test_110718.xsl})"     ! customXslLoad ^
			"must load the XSL stylesheet via the the REST API (${bs_full_test_110718.xsl})"         ! customXslLoad ^
			"must load the XSL stylesheet via the the REST API (${bs_simple_test_110718.xsl})"       ! customXslLoad ^
																									   endp ^
			"must succeed in loading the sample metadata"  											 ^ Step(importMetadataId) ^
			"must correctly transform the inserted sample MD using ${bs_extended_test_110718.xsl}"	 ! testXslCustomTransform ^	
			"must correctly transform the inserted sample MD using ${bs_full_test_110718.xsl}"	 	 ! testXslCustomTransform ^	
			"must correctly transform the inserted sample MD using ${bs_simple_test_110718.xsl}"	 ! testXslCustomTransform ^	
			"Delete the inserted metadata"															 ^ Step(deleteMetadata)  ^
			"Removes the previously inserted user XSL stylesheets"									 ^ Step(deleteXslStyleSheets)  ^
																									   end ^ Step(tearDown)	
			
  lazy val importMetadataId = {
    	val name = "metadata.iso19139.che.xml"
    	val (_,content) = ResourceLoader.loadDataFromClassPath("/geocat/data/"+name, getClass, uuid)
    	val ImportMd = ImportMetadata.findGroupId(content,NONE,true)
    
    	val md = (ImportMd then GetRawMetadataXml)(ImportStyleSheets.NONE).value.getXml
    	val response = (md \\ "fileIdentifier").text.trim
    	response
  	}

    def deleteMetadata = {
		  GetRequest("metadata.delete", ("uuid" -> importMetadataId))(Nil)
    }
    def deleteXslStyleSheets= {
	  List("bs_extended_test_110718.xsl",
	       "bs_full_test_110718.xsl",
	       "bs_simple_test_110718.xsl").foreach { n => GetRequest("metadata.xsl.remove", ("id" -> xslId(n)))(Nil) }
    }
    
    
	def xslId(xslFileName: String) = uuid.toString+xslFileName.dropRight(4)
	
    def customXslLoad = (descriptor : String) => {
    	val name = extract1(descriptor)
    	val (_,content) = ResourceLoader.loadDataFromClassPath("/geocat/customxsl/"+name, getClass, uuid)
    	val restRequest = new AbstractMultiPartFormRequest[Any,XmlValue]("metadata.xsl.register", 
    																	 XmlValueFactory, 
    																	 P("file", content),
    																	 P("id", new StringBody(xslId(name)))){}
	    restRequest()  must haveA200ResponseCode
    }
    def testXslCustomTransform = (desc:String) => {
    	def name = extract1(desc)
		GetRequest("metadata.formatter.html", "uuid" -> importMetadataId, "xsl" -> xslId(name))(Nil) must haveA200ResponseCode
      
    }
			
}
