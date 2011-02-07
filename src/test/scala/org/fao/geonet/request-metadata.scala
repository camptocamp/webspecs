package org.fao.geonet

import java.io.{InputStreamReader, BufferedReader, InputStream, File}
import xml.{Node, Elem, NodeSeq}

private[geonet] object MdRequestUtil {
  def withXml[R](response:Response)(f:NodeSeq => R):R = response.xml.fold(throw _,f)

  private def idFail(r:Request) = throw new IllegalArgumentException("Don't know how to get an id from "+r)
  def loadManyIds[R<:Request](creator: Seq[Int] => R):Response => R = (res:Response) => {

    res.request match {
      case req:GetMetadataXml => creator(List(req.mdId))
      case req:ImportMetadata =>
        withXml(res){
          xml =>
            val okNode = (xml \\ "ok").headOption getOrElse {throw new IllegalArgumentException("response does not have an <ok> tag, are you sure the response is from a import metadata request?")}

            val ids = okNode.text.split(";").map{_.trim}.filter{_.nonEmpty}
            creator(ids map {_.toInt} toList)
        }
      case req:EditRequest =>
        withXml(res){
          xml =>
            val allInput = xml \\ "input"
            val idInputEl = allInput filter {e => (e \ "@name").text == "id"}
            val values = idInputEl map {_.attribute("value").get.text}
            val id = values.headOption getOrElse {
              throw new IllegalStateException("Expected to find an id input element. Does the response come from a request that returns a metadata.edit form? ")
            }
            creator(List(id.toInt))
        }
      case req:AbstractRequest[_] =>
        val id = req.queryParams.get("id") getOrElse {idFail(req)}
        creator(List(id.toInt))
    }
  }
  def loadId[R<:Request](creator: Int => R) = loadManyIds{ids =>
    assert(ids.size == 1,"Expected one and only one id")
    creator(ids.head)
  }
}

object MetadataViews extends Enumeration {
  type MetadataView = Value
  val xml, simple = Value
}
import MetadataViews.MetadataView
object ShowMetadata {
  def apply(view:MetadataView = MetadataViews.xml):Response => ShowMetadata = MdRequestUtil.loadId(id => ShowMetadata(id,view))
}
case class ShowMetadata(mdId:Int, view:MetadataView) extends GetRequest("metadata.show", XmlResponseFactory, "id" -> mdId, "currTab" -> view)
object DeleteMetadata {
  def apply():Response => DeleteMetadata = MdRequestUtil.loadId(id => DeleteMetadata(id))
}
case class DeleteMetadata(mdId:Int) extends GetRequest("metadata.delete", XmlResponseFactory, "id" -> mdId)

object GetEditingMetadata {
  def apply(): Response => GetEditingMetadata = MdRequestUtil.loadId(id => GetEditingMetadata(id))
}
case class GetEditingMetadata(mdId:Int) extends GetRequest("metadata.ext.edit.data", XmlResponseFactory, "id" -> mdId)
object GetMetadataXml {
  def apply(schema:OutputSchemas.OutputSchema = OutputSchemas.CheRecord):Response => GetMetadataXml = MdRequestUtil.loadId(id => GetMetadataXml(id,schema))
}
case class GetMetadataXml(mdId:Int, schema:OutputSchemas.OutputSchema) extends Request {
  private val getRequest = this
  private val csw = CswGetRecordsRequest(PropertyIsEqualTo("_id",mdId.toString).xml,ResultTypes.results,schema)
  def exec(sideEffect: Option[SideEffect]):Response = {
    new ExtractMdXmlResponse(csw.exec((sideEffect)))
  }
  private final class ExtractMdXmlResponse(cswResponse:Response) extends Response with XmlResponse with MetadataIdResponse{
    lazy val xml = cswResponse.xml match {
      case Right(xmlData) =>
        util.control.Exception.catching(classOf[Throwable]) either {
          val resultsElem = (xmlData \\ "SearchResults")
          assert(resultsElem.size > 0, "CswGetRecords returned no SearchResults Elem metadataId: "+mdId)
          assert(resultsElem.size == 1, "CswGetRecords returned too many SearchResults Elements ("+resultsElem.size+") for metadataId: "+mdId)
          val results = resultsElem.head.child.collect{case n:Elem => n}
          assert(results.size > 0, "CswGetRecords did not return any results for metadataId: "+mdId)
          assert(results.size == 1, "CswGetRecords returned too many results ("+results.size+") for metadataId: "+mdId)
          results.head
        }
      case left => left
    }

    val id = mdId

    lazy val text = xml match {
      case Left(error) => Left(error)
      case Right(xml) => Right(xml.toString)
    }
    val contentLength = cswResponse.contentLength
    val allHeaders = cswResponse.allHeaders
    val responseMessage = cswResponse.responseMessage
    val responseCode = cswResponse.responseCode
    val effect = cswResponse.effect
    val request = getRequest
  }
}
object ImportStyleSheets extends Enumeration {
  type ImportStyleSheet = Value
  val GM03_V1 = Value("GM03-to-ISO19139CHE.xsl")
  val GM03_V2 = Value("GM03_2-to-ISO19139CHE.xsl")
  val ISO = Value("ISO19139-to-ISO19139CHE.xsl")
  val NONE = Value("_none_")
}
object ImportMetadata {
  def apply (fileName:String, data:String, styleSheet:ImportStyleSheets.ImportStyleSheet, validate:Boolean)(implicit constants:Constants):ImportMetadata = {
     ImportMetadata(fileName, data, styleSheet,validate,constants.groupId)
  }
  def apply (fileName:String, data:InputStream, styleSheet:ImportStyleSheets.ImportStyleSheet, validate:Boolean)(implicit constants:Constants):ImportMetadata = {
    val reader = new BufferedReader(new InputStreamReader(data))
    try{
      var line = reader.readLine
      val bf = new StringBuilder()
      while(line != null){
        bf ++= line
        bf += '\n'
        line = reader.readLine()
      }
      val string = bf.toString
      ImportMetadata(fileName, string, styleSheet,validate,constants.groupId)
    } finally {
      reader.close()
    }
  }
}
case class ImportMetadata(fileName:String, data:String, styleSheet:ImportStyleSheets.ImportStyleSheet, validate:Boolean, groupId:String)
  extends MultiPartFormRequest(
    "mef.import",
    XmlResponseFactory,
    FieldMPFormPart("insert_mode", "1"),
    FieldMPFormPart("file_type", "single"),
    FileMPFormPart("mefFile", "application/xml", fileName, data.getBytes("UTF-8").iterator),
    FieldMPFormPart("uuidAction", "generateUUID"), //other options: nothing,
    FieldMPFormPart("template", "n"),
    FieldMPFormPart("styleSheet", styleSheet.toString),
    FieldMPFormPart("group", groupId),
    FieldMPFormPart("category", "_none_"),
    FieldMPFormPart("validate", (if(validate)"on" else "off"))
  )
