package c2c.webspecs
package geonetwork

import java.io.{InputStreamReader, BufferedReader, InputStream, File}
import xml.{Node, Elem, NodeSeq}
import org.apache.http.entity.mime.content.{AbstractContentBody, ByteArrayBody, FileBody, StringBody}
import java.net.URL
import scalax.file.Path
import scalax.io.{Codec, Resource}

trait MetadataValue extends IdValue

object IdValuesFactory {
  object FromImportOrCreateResult extends BasicValueFactory[IdValue]{
    override def createValue(rawValue: BasicHttpValue) =
        new XmlValue with IdValue {
          val basicValue = rawValue
          lazy val id = withXml { xml =>
              // early versions < 2.6
              val okNode = (xml \\ "ok").headOption

              // more recent versions > 2.6
              val idNode = (xml \\ "id").headOption

              val nodes = (okNode orElse idNode).map{_.text.split(";").map{_.trim}.filter{_.nonEmpty}}.toList.flatten

              nodes.headOption getOrElse {
                  throw new IllegalStateException("Expected to find an id or ok element. Does the response come from a create or import request?")
              }
          }
        }
  }
  object FromEditResult extends BasicValueFactory[IdValue]{
    override def createValue(rawValue: BasicHttpValue) =
        new XmlValue with IdValue {
          val basicValue = rawValue
          lazy val id = withXml { xml =>
            val allInput = xml \\ "input"
            val idInputEl = allInput filter {e => (e \ "@name").text == "id"}
            val values = idInputEl map {_.attribute("value").get.text}
            values.headOption getOrElse {
              throw new IllegalStateException("Expected to find an id input element. Does the response come from a request that returns a metadata.edit form? ")
            }
          }
        }
  }
}

object MetadataViews extends Enumeration {
  type MetadataView = Value
  val xml, simple = Value
}
import MetadataViews.MetadataView

case class ShowMetadata(view:MetadataView = MetadataViews.xml)
  extends AbstractGetRequest[Id,IdValue](
    "metadata.show",
    InputTransformerValueFactory( (in,basic) =>
      new XmlValue with IdValue {
        protected def basicValue = basic
        def id = in.id
      }
    ),
    InP("id", _.id),
    P("currTab", view.toString))

case object GetEditingMetadata extends AbstractGetRequest[Id,IdValue](
  "metadata.ext.edit.data",
  InputTransformerValueFactory((in,basic) =>
    new IdValue with XmlValue {
      protected def basicValue = basic
      def id = in.id
    }
  ),
  InP("id", _.id))

case class GetMetadataXml(schema:OutputSchemas.OutputSchema = OutputSchemas.IsoRecord) extends Request[Id,MetadataValue] {
  private val getRequest = this
  private def csw(id:String) = CswGetRecordsRequest(PropertyIsEqualTo("_id",id).xml,ResultTypes.results,schema)

  override def apply(in: Id)(implicit context: ExecutionContext):Response[MetadataValue] = {
    val metadataXml: BasicHttpResponse[XmlValue] = csw(in.id)(None)
    new ExtractMdXmlResponse(in.id,metadataXml)
  }

  private final class ExtractMdXmlResponse(mdId:String, cswResponse:Response[XmlValue]) extends Response[MetadataValue] {

    def basicValue = cswResponse.basicValue
    val value = new MetadataValue {
      def basicValue = cswResponse.basicValue
      val id = mdId
      override lazy val xml = cswResponse.value.xml match {
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

      override lazy val text = xml match {
        case Left(error) => Left(error)
        case Right(xml) => Right(xml.toString)
      }
    }
  }
}
object ImportStyleSheets {
  abstract class ImportStyleSheet(val name:String) {
    override def toString: String = name
  }
  case object ISO extends ImportStyleSheet("ISO19139-to-ISO19139CHE.xsl")
  case object NONE extends ImportStyleSheet("_none_")
}
object ImportMetadata {
  def importData (resource:URL,fileName:String="") = {
    val string = Resource.fromURL(resource).slurpString(Codec.UTF8)

    val name =
      if (fileName.trim() == "") Path.fromString(resource.getFile).name
      else fileName

    (string, new ByteArrayBody(string.getBytes(Codec.UTF8.charSet),"application/xml",name))
  }
  def importDataFromClassPath(file:String,cl:Class[_]) =
    importData(cl.getResource(file))

  def findGroupId (data:AbstractContentBody, styleSheet:ImportStyleSheets.ImportStyleSheet, validate:Boolean)(implicit config:GeonetConfig):ImportMetadata = {
     ImportMetadata(data, styleSheet,validate,config.groupId)
  }
}
case class ImportMetadata(data:AbstractContentBody, styleSheet:ImportStyleSheets.ImportStyleSheet, validate:Boolean, groupId:String)
  extends AbstractMultiPartFormRequest[Any,IdValue](
    "mef.import",
    IdValuesFactory.FromImportOrCreateResult,
    P("insert_mode",  new StringBody("1")),
    P("file_type",  new StringBody("single")),
    P("mefFile",  data),
    P("uuidAction",  new StringBody("generateUUID")), //other options: nothing,
    P("template",  new StringBody("n")),
    P("styleSheet",  new StringBody(styleSheet.toString)),
    P("group",  new StringBody(groupId)),
    P("category",  new StringBody("_none_")),
    P("validate",  new StringBody(if(validate)"on" else "off"))
  )
