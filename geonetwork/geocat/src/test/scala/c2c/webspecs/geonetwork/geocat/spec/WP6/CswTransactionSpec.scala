package c2c.webspecs
package geonetwork
package geocat
package spec.WP6

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step

import csw._

@RunWith(classOf[JUnitRunner])
class CswTransactionSpec extends GeocatSpecification {
  def is = {
    "CSW GetCapabilities services URL".title ^ sequential ^ Step(setup) ^ 
      "CswInsert should insert a new record" ! CswInsert ^
      "CswUpdate should update the inserted record with a new metadata" ! CswFullUpdate ^
      "CswUpdate should update the parts of the record with partial updates" ! CswPartialUpdate ^
      "CswUpdate should update the parts of the record with partial with new XML" ! CswPartialUpdateXml ^
      "CswDelete should delete the inserted record" ! CswDelete ^
      end ^ Step(tearDown)
  }

  override def extraTeardown(teardownContext: ExecutionContext): Unit = {
    super.extraTeardown(teardownContext)
    GetRequest("metadata.delete", "uuid" -> uuid).execute()(teardownContext,uriResolver)
  }

  val data = ResourceLoader.loadDataFromClassPath("/geocat/data/comprehensive-iso19139che.xml", classOf[CswTransactionSpec], uuid)._1
  def getData(abstractData: String) = data.replaceAll("\\{abstract}", abstractData)

  def CswInsert = {
    val initialData = "initial data"
    val response = CswTransactionInsert(getData(initialData)).execute()

    (response must haveA200ResponseCode) and
      newMetadataMustExistWith(initialData)
  }

  def CswFullUpdate = {
    val updatedData = "updated data"
    val response = CswTransactionFullUpdate(getData(updatedData)).execute()
    
    (response must haveA200ResponseCode) and
      newMetadataMustExistWith(updatedData)
  }
  
  def CswPartialUpdate = {
    val updatedData = "updated data 2"
    val response = CswTransactionUpdate(uuid.toString, ".//gmd:identificationInfo//gmd:abstract//gmd:LocalisedCharacterString[1]" -> updatedData).execute()
    (response must haveA200ResponseCode) and
      newMetadataMustExistWith(updatedData)
  }
  def CswPartialUpdateXml = {
    val charString = "CharString Abstract"
    val deString = "DE PTFree Text"
    val updatedData =
      <gmd:abstract xsi:type="gmd:PT_FreeText_PropertyType" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gmd="http://www.isotc211.org/2005/gmd">
        <gco:CharacterString>{charString}</gco:CharacterString>
        <gmd:PT_FreeText>
          <gmd:textGroup>
            <gmd:LocalisedCharacterString locale="#DE">{deString}</gmd:LocalisedCharacterString>
          </gmd:textGroup>
        </gmd:PT_FreeText>
      </gmd:abstract>
    val response = CswTransactionUpdate(uuid.toString, ".//gmd:identificationInfo//gmd:abstract" -> updatedData).execute()
    val getResponse = CswGetRecordById(uuid.toString(), geocat.OutputSchemas.CheIsoRecord).execute()

    val md = getResponse.value.getXml
    println(md)

    (response must haveA200ResponseCode) and
      ((md \\ "abstract" \ "CharacterString").text must_== charString) and
      ((md \\ "abstract" \ "PT_FreeText" \\ "LocalisedCharacterString").text must_== deString)
      ((md \\ "abstract" \ "PT_FreeText" \\ "LocalisedCharacterString" \@ "locale")(0) must_== "#DE")
  }

  def newMetadataMustExistWith(abstractData: String, positiveCheck: Boolean = true) = {
    val response = CswGetRecordById(uuid.toString(), OutputSchemas.DublinCore).execute()

    val results = response.value.getXml \\ "SummaryRecord"

    (results must haveSize(1)) and
    	((results \\ "abstract").text.trim must_== abstractData)
  }

  def newMetadataMustNotExist = {
    val response = CswGetRecordById(uuid.toString(), OutputSchemas.DublinCore).execute()
    val results = response.value.getXml \\ "SummaryRecord"
    results must beEmpty
  }

  def CswDelete = {
    val response = CswTransactionDelete(uuid.toString).execute()

    (response must haveA200ResponseCode) and
      (newMetadataMustNotExist)
  }

}