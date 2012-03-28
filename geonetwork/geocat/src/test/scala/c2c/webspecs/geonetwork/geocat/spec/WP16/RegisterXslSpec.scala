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
import c2c.webspecs.GetRequest


@RunWith(classOf[JUnitRunner]) 
class RegisterXslSpec extends GeocatSpecification() {  def is =
	"Xsl custom metadata XML output".title 															 ^ Step(setup) ^
			"Login as admin"																		 ^ Step(config.adminLogin.execute()) ^
			"must load the XSL stylesheet via the the REST API (${webspecs_extended_test.xsl})"     ! customXslLoad ^
			"must load the XSL stylesheet via the the REST API (${webspecs_full_test.xsl})"         ! customXslLoad ^
			"must load the XSL stylesheet via the the REST API (${webspecs_simple_test.xsl})"       ! customXslLoad ^
																									   endp ^
			"must succeed in loading the sample metadata"  											 ^ Step(importMetadataId) ^
			"must correctly transform the inserted sample MD using ${webspecs_extended_test.xsl}"	 ! testXslCustomTransform ^	
			"must correctly transform the inserted sample MD using ${webspecs_full_test.xsl}"	 	 ! testXslCustomTransform ^	
			"must correctly transform the inserted sample MD using ${webspecs_simple_test.xsl}"	 ! testXslCustomTransform ^	end ^
																									   Step(tearDown)	
			
  lazy val importMetadataId = {
    val importId = importMd(1,"/geocat/data/metadata.iso19139.che.xml",uuid.toString).head
    val mdValue = GetRawMetadataXml.execute(importId)

    (mdValue.value.getXml \\ "fileIdentifier").text.trim
  }

	def xslId(xslFileName: String) = uuid.toString+xslFileName.dropRight(4)
	
    def customXslLoad = (descriptor : String) => {
    	val name = extract1(descriptor)
    	val (_,content) = ResourceLoader.loadDataFromClassPath("/geocat/customxsl/"+name, getClass, uuid)
    	val restRequest = new AbstractMultiPartFormRequest[Any,XmlValue]("metadata.xsl.register", 
    																	 XmlValueFactory, 
    																	 P("fname", content),
    																	 P("id", new StringBody(xslId(name)))){}
	    restRequest.execute()  must haveA200ResponseCode
    }
    def testXslCustomTransform = (desc:String) => {
    	def name = extract1(desc)
		GetRequest("metadata.formatter.html", "uuid" -> importMetadataId, "xsl" -> xslId(name)).execute() must haveA200ResponseCode
    }
			
    override def extraTeardown(teardownContext:ExecutionContext):Unit = {
      import util.control.Exception._
      allCatch(deleteXslStyleSheets)
      allCatch(super.extraTeardown(teardownContext))
    }
    def deleteXslStyleSheets= {
	  List("webspecs_extended_test.xsl",
	       "webspecs_full_test.xsl",
	       "webspecs_simple_test.xsl").foreach { n => GetRequest("metadata.xsl.remove", ("id" -> xslId(n))).execute()  must haveA200ResponseCode }
    }
}
