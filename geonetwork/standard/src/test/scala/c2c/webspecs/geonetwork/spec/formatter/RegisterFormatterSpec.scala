package c2c.webspecs
package geonetwork
package spec.formatter

import org.apache.http.entity.mime.content.StringBody
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import c2c.webspecs.XmlValue
import c2c.webspecs.GetRequest
import org.apache.http.entity.mime.content.InputStreamBody
import java.io.File


@RunWith(classOf[JUnitRunner]) 
class RegisterFormatterSpec extends GeonetworkSpecification() {  def is =
	"Xsl custom metadata XML output".title 															 ^ Step(setup) ^ Step(importMetadataId) ^ sequential ^
    	"Login as admin"																		 ^ Step(config.adminLogin.execute()) ^ endp ^
    	"must load the XSL stylesheet via the the REST API (${webspecs_single_file.xsl})"        ! customXslLoad ^ p ^
            "must successfully transform the inserted sample MD using ${webspecs_single_file}"   ! testXslCustomTransform ^
            "${webspecs_single_file} makes ${eng} translation when executed with ${eng} gui language"       ! homeLanguage ^ 
            "${webspecs_single_file} makes ${fre} translation when executed with ${fre} gui language"       ! homeLanguage ^ 
            "Downloading ${webspecs_single_file} contains a config file, view.xsl file and loc folder"      ! download ^ endp ^
    	"must load the XSL stylesheet via the the REST API (${webspecs_localized_flatzip.zip})"  ! customXslLoad ^ p ^
            "must successfully transform the inserted sample MD using ${webspecs_localized_flatzip}"   ! testXslCustomTransform ^
            "${webspecs_localized_flatzip} makes ${eng} translation when executed with ${eng} gui language"       ! homeLanguage ^ 
            "${webspecs_localized_flatzip} makes ${fre} translation when executed with ${fre} gui language"       ! homeLanguage ^ 
            "${webspecs_localized_flatzip} makes ${eng} translation from bundled loc files when executed with ${eng} gui language"       ! bundledLanguage ^ 
            "${webspecs_localized_flatzip} makes ${fre} translation from bundled loc files when executed with ${fre} gui language"       ! bundledLanguage ^ 
            "Downloading ${webspecs_localized_flatzip} contains a config file, view.xsl file and loc folder"      ! download ^
            "${webspecs_localized_flatzip} has correct image url"                                            ! img ^ endp ^ 
    	"must load the XSL stylesheet via the the REST API (${webspecs_localized_folder.zip})"          ! customXslLoad ^ p ^
            "must successfully transform the inserted sample MD using ${webspecs_localized_folder}"   ! testXslCustomTransform ^
            "${webspecs_localized_folder} makes ${eng} translation when executed with ${eng} gui language"       ! homeLanguage ^ 
            "${webspecs_localized_folder} makes ${fre} translation when executed with ${fre} gui language"       ! homeLanguage ^ endp ^
            "${webspecs_localized_folder} makes ${eng} translation from bundled loc files when executed with ${eng} gui language"       ! bundledLanguage ^ 
            "${webspecs_localized_folder} makes ${fre} translation from bundled loc files when executed with ${fre} gui language"       ! bundledLanguage ^ 
            "Downloading ${webspecs_localized_folder} contains a config file, view.xsl file and loc folder"      ! download ^
            "${webspecs_localized_folder} has correct image url"                                            ! img ^ endp ^ 
    	"must load the XSL stylesheet via the the REST API (${webspecs_fixed_locale.zip})"       ! customXslLoad ^ p ^
            "must successfully transform the inserted sample MD using ${webspecs_fixed_locale}"   ! testXslCustomTransform ^
            "${webspecs_fixed_locale} makes ${eng} translation when executed with ${eng} gui language"       ! homeLanguage ^ 
            "${webspecs_fixed_locale} makes ${eng} translation when executed with ${eng} gui language"       ! homeLanguage ^ endp ^ 
            "${webspecs_fixed_locale} makes ${eng} translation from bundled loc files when executed with ${eng} gui language"       ! bundledLanguage ^ 
            "${webspecs_fixed_locale} makes ${eng} translation from bundled loc files when executed with ${fre} gui language"       ! bundledLanguage ^ 
            "Downloading ${webspecs_fixed_locale} contains a config file, view.xsl file and loc folder"      ! download ^
            "${webspecs_fixed_locale} has correct image url"                                            ! img ^ endp ^ 
    																							   endp ^
        "delete ${webspecs_single_file} correctly removes formatter"                             ! deleteXslStyleSheet ^
        "delete ${webspecs_fixed_locale} correctly removes formatter"                            ! deleteXslStyleSheet ^
        "delete ${webspecs_localized_flatzip} correctly removes formatter"                       ! deleteXslStyleSheet ^
        "delete ${webspecs_localized_folder} correctly removes formatter"                               ! deleteXslStyleSheet ^ end ^
																									   Step(tearDown)

  def xmlFile = "/geonetwork/data/valid-metadata.iso19139.xml"

  lazy val importMetadataId = {
    val importId = importMd(1, xmlFile, uuid.toString).head
    val mdValue = GetRawMetadataXml.execute(importId)

    (mdValue.value.getXml \\ "fileIdentifier").text.trim
  }

  def xslId(xslFileName: String) = uuid.toString + xslFileName

  def customXslLoad = (descriptor: String) => {
    val name = extract1(descriptor)
    val content = new InputStreamBody(classOf[RegisterFormatterSpec].getResourceAsStream("/geonetwork/customxsl/" + name), "application/zip")
    val id = xslId(name.dropRight(4))
    val registerResponse = MultiPartFormRequest("metadata.formatter.register",
      'fname -> content,
      'id -> new StringBody(id)).execute()
      
    val findResult = findFormatter(name.dropRight(4))
    (registerResponse must haveA200ResponseCode) and
      (findResult must beSome)
  }
  val testXslCustomTransform = (desc: String) => {
    def format = extract1(desc)
    (GetRequest("metadata.formatter.html", "uuid" -> importMetadataId, "xsl" -> xslId(format)).execute() must haveA200ResponseCode) and 
        (GetRequest("metadata.formatter.xml", "uuid" -> importMetadataId, "xsl" -> xslId(format)).execute() must haveA200ResponseCode) 
  }
  
  val homeLanguage = (s:String) => {
    val (format,expectedLang,requestLang) = extract3(s)
    val strings = expectedLang match {
      case "eng" => engStrings
      case "fre" => freStrings
    }
    
    (xml(requestLang, format) \\ "s1" \ "@v").text must_== (strings \ "home").text
  } 
  
  val download = (s:String) => {
    val FS = File.separator
    val format = extract1(s)
    val id = xslId(format)
    val zipValue = GetRequest("metadata.formatter.download", 'id -> id).execute().basicValue.toZipValue
    val fileNames = zipValue.fileNames 
    (fileNames must contain(id+FS+"config.properties")) and
        (fileNames must contain (id+FS+"view.xsl")) and
        (fileNames.exists{_.startsWith(id+FS+"loc")} aka "Loc folder or subFile" must beTrue)
  }
  
  val bundledLanguage = (s:String) => {
      val (format,expectedLang,requestLang) = extract3(s)
              val string = expectedLang match {
              case "eng" => "String"
              case "fre" => "Chaine"
      }
      (xml(requestLang, format) \\ "s2" \ "@v").text must_== string
  } 

  val img = (s:String) => {
    val format = extract1(s)
    val formatXml = xml("eng", format)
    val imgURL = (formatXml \\ "img" \ "@src").text

    (formatXml must \\("img")) and
    	(imgURL.trim must not(beEmpty)) and
    	(GetRequest(imgURL.split("/").last).execute() must haveA200ResponseCode)
  } 


  val deleteXslStyleSheet = (s: String) => {
    val sheet = extract1(s)
    (GetRequest("metadata.formatter.remove", ("id" -> xslId(sheet))).execute() must haveA200ResponseCode) and
      (findFormatter(sheet) must beNone)
  }

  def findFormatter(format: String) = {
    val response = GetRequest("metadata.formatter.list").execute().value.getXml
    val formatters = response \ "formatter" map (_.text)
    val id = xslId(format)
    formatters.find(_ == id)
  }

  lazy val freStrings = {
    val xml = GetRequest("fre/home!").execute().value.getXml
    xml \ "gui" \ "strings"
  }
  lazy val engStrings = GetRequest("eng/home!").execute().value.getXml \ "gui" \ "strings"
  
  def xml(requestLang:String, format:String) = {
    val response = GetRequest(requestLang+"/metadata.formatter.xml", "uuid" -> importMetadataId, "xsl" -> xslId(format)).execute()
    assert(response.basicValue.responseCode == 200, "Expected 200 response code not "+response.basicValue.responseCode)
    response.value.getXml
  }
}
