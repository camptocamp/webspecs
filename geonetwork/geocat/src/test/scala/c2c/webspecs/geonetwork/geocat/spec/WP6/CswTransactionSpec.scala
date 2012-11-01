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