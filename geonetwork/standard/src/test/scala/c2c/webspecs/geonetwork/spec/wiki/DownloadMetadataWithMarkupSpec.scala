package c2c.webspecs
package geonetwork
package spec.wiki

import csw._
import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml.NodeSeq
import scala.xml.XML
import scala.xml.Elem

@RunWith(classOf[JUnitRunner])
class DownloadMetadataWithMarkupSpec extends GeonetworkSpecification {
  def is =
    Step(setup) ^ Step(importTestData) ^ Step(getWikiSettings) ^ sequential ^
      "Configure System to Strip markup from metadata for mef and download" ^ Step(setWikiSetting("org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage", "strip", "strip")) ^
      "Download MEF file and verify metadata ${has no} markup" ! mefNoMarkup ^
      "Download using CSW GetRecords and verify metadata ${has no} markup" ! cswGetRecordsNoMarkup ^
      "Download using CSW GetRecordById and verify metadata ${has no} markup" ! cswGetRecordByIdNoMarkup ^
      "Export as iso19139 and verify metadata ${has no} markup" ! iso19139NoMarkup ^
      "Export as DublinCore and verify metadata ${has no} markup" ! ai_dcNoMarkup ^
      "Export as RDF and verify metadata ${has no} markup" ! rdfNoMarkup ^
      Step(resetWikiSetting)^
      Step(tearDown)

  def datapath = "/geonetwork/data/iso19139-with-markup.xml"
  lazy val importTestData = {
      val ids = importMd(1, datapath, uuid.toString)
      val actualUuid = (CswGetRecordsRequest(PropertyIsEqualTo("_id", ids.head.id).xml, resultType = ResultTypes.results, outputSchema = OutputSchemas.Record).execute().value.getXml \\ "identifier").text
      (ids.head, actualUuid)
  }
  
  def mdId = importTestData._1
  def mdUUID = importTestData._2
  
  val mefNoMarkup = (spec: String) => {
    val mef = GetRequest("mef.export", 'version -> 2, 'uuid -> mdUUID).execute().basicValue.toZipValue
    val md = mef.files.find(_.getName endsWith "metadata.xml").map{
      zipFile => 
        val mdString = new String(mef.file(zipFile), "UTF-8")
        XML.loadString(mdString)
    }
    
    checkMd(spec, md.get)
  }
  val cswGetRecordsNoMarkup = (spec: String) => {
      val md = CswGetRecordsRequest(
          PropertyIsEqualTo("_id", mdId.id).xml,
          outputSchema = OutputSchemas.IsoRecord,
          resultType = ResultTypes.results).execute().value.getXml

     val dublinCoreMd = CswGetRecordsRequest(
          PropertyIsEqualTo("_id", mdId.id).xml,
          outputSchema = OutputSchemas.Record,
          resultType = ResultTypes.results).execute().value.getXml

     checkDublinCoreMd(spec, dublinCoreMd) and checkMd(spec, md)
  }
  val cswGetRecordByIdNoMarkup = (spec: String) => {
      val md = CswGetRecordById(
          mdUUID,
          outputSchema = OutputSchemas.IsoRecord).execute().value.getXml

     val dublinCoreMd = CswGetRecordById(
          mdUUID,
          outputSchema = OutputSchemas.Record).execute().value.getXml

      checkDublinCoreMd(spec, dublinCoreMd) and checkMd(spec, md)
  }
  val iso19139NoMarkup = (spec: String) => {
    val rawMd = GetRawMetadataXml.execute(mdId).value.getXml
    val exportedMd = GetRequest("xml_iso19139", 'id -> mdId.id).execute().value.getXml
    
    checkMd(spec, rawMd) and 
        checkMd(spec, exportedMd)
  }
  val ai_dcNoMarkup = (spec: String) => {
      checkDublinCoreMd(spec, GetRequest("xml_iso19139Tooai_dc", 'id -> mdId.id, 'styleSheet -> "oai_dc.xsl").execute().value.getXml, "description")
  }

  val rdfNoMarkup = (spec: String) => {
      val md = GetRequest("rdf.metadata.get", 'id -> mdId.id).execute().value.getXml
      
      checkDublinCoreMd(spec, md)
  }

  def checkDublinCoreMd(spec:String, md: NodeSeq, tag:String = "abstract") = {
    val abstractText = (md \\ tag).text
    val check = extract1(spec) match {
      case "has no" =>
        (abstractText must not(contain("'''ISO19115 metadata standard'''"))) and
          (abstractText must not(contain("'''''La norme'''''")))
      case "has" =>
        (abstractText must contain("'''ISO19115 metadata standard'''"))
    }
    
      (abstractText must not (beEmpty)) and check
  }
  
  def checkMd(spec:String, md: NodeSeq) = {
      val abstractText = (md \\ "abstract").text
      val check = extract1(spec) match {
        case "has no" =>
            (abstractText must not(contain("'''ISO19115 metadata standard'''"))) and
              (abstractText must not(contain("'''''La norme'''''")))
        case "has" =>
            (abstractText must contain("'''ISO19115 metadata standard'''")) and
              (abstractText must contain("'''''La norme'''''"))
      }
      
      (abstractText must not (beEmpty)) and check
  }

  lazy val getWikiSettings = ExecutionContext.withDefault{ implicit context =>
    config.adminLogin.execute()
    GetRequest("xml.config.get").execute().value.getXml
  }
  def doSetWikiSettings(settings:NodeSeq) = ExecutionContext.withDefault{ implicit context =>
    config.adminLogin.execute()
    val data: NodeSeq = <config>{getWikiSettings \ "site"}{settings}</config>
    XmlPostRequest("xml.config.set", data).execute() must haveA200ResponseCode
  }
  def resetWikiSetting = doSetWikiSettings(getWikiSettings \ "wiki")
  /**
   * @param markup valid settings for this test are: 'none' and 'org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage'
   * @param output and mefOutput valid settings are: 'strip' and 'keep' 
   */
  def setWikiSetting(markup:String, output:String, mefOutput:String) = {
    doSetWikiSettings(<wiki>
       <markup>{markup}</markup>
       <output>{output}</output>
       <mefoutput>{mefOutput}</mefoutput>
   </wiki>)
  }

}