package c2c.webspecs
package geonetwork

import java.io.{InputStreamReader, BufferedReader, InputStream, File}
import xml.{Node, Elem, NodeSeq}
import org.apache.http.entity.mime.content.{FileBody, StringBody}

trait MetadataValue extends IdValue

object IdValuesFactory {
  object FromImportOrCreateResult extends ValueFactory[Any,IdValue]{
    def apply[A <: Any, B >: IdValue](request: Request[A, B], in: Any, rawValue: BasicHttpValue) =
        new XmlValue with IdValue {
          val basicValue = rawValue
          lazy val id = withXml { xml =>
              // early versions < 2.6
              val okNode = (xml \\ "ok").headOption getOrElse {throw new IllegalArgumentException("response does not have an <ok> tag, are you sure the response is from a import metadata request?")}

              // more recent versions > 2.6
              val idNode = (xml \\ "id").headOption getOrElse {throw new IllegalArgumentException("response does not have an <id> tag, are you sure the response is from a import metadata request?")}

              def parse(nodes:NodeSeq) = okNode.text.split(";").map{_.trim}.filter{_.nonEmpty}
              val idStrings = parse(okNode) ++ parse(idNode)

              idStrings.headOption getOrElse {
                  throw new IllegalStateException("Expected to find an id or ok element. Does the response come from a create or import request?")
              }
          }
        }
  }
  object FromEditResult extends ValueFactory[Any,IdValue]{
    def apply[A <: Any, B >: IdValue](request: Request[A, B], in: Any, rawValue: BasicHttpValue) =
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
object MetadataRequest {
  def makeInputBasedMetadataRequest[Out](factory:String => Request[IdValue,Out]) = new Request[IdValue,Out] {
    def apply(in: IdValue)(implicit context: ExecutionContext) = factory(in.id)(in)
  }
}
object ShowResultingMetadata {
  def apply(view:MetadataView = MetadataViews.xml) = MetadataRequest.makeInputBasedMetadataRequest(id => ShowMetadata(id,view))
}
case class ShowMetadata(mdId:String, view:MetadataView = MetadataViews.xml) extends AbstractGetRequest[Any,IdValue]("metadata.show", ExplicitIdValueFactory(mdId), "id" -> mdId, "currTab" -> view.toString)
object DeleteResultingMetadata {
  def apply() = MetadataRequest.makeInputBasedMetadataRequest(id => DeleteMetadata(id))
}
case class DeleteMetadata(mdId:String) extends AbstractGetRequest(
  "metadata.delete",
  ExplicitIdValueFactory(mdId),
  "id" -> mdId)
object GetEditingMetadataFromResult {
  def apply() = MetadataRequest.makeInputBasedMetadataRequest(id => GetEditingMetadata(id))
}
case class GetEditingMetadata(mdId:String) extends AbstractGetRequest[Any,IdValue](
  "metadata.ext.edit.data",
  ExplicitIdValueFactory(mdId),
  "id" -> mdId)
object GetMetadataXmlFromResult {
  def apply(schema:OutputSchemas.OutputSchema = OutputSchemas.IsoRecord) =
    MetadataRequest.makeInputBasedMetadataRequest(id => GetMetadataXml(id,schema))
}
case class GetMetadataXml(mdId:String, schema:OutputSchemas.OutputSchema = OutputSchemas.IsoRecord) extends Request[Any,MetadataValue] {
  private val getRequest = this
  private val csw = CswGetRecordsRequest(PropertyIsEqualTo("_id",mdId.toString).xml,ResultTypes.results,schema)

  override def apply(in: Any)(implicit context: ExecutionContext):Response[MetadataValue] = {
    new ExtractMdXmlResponse(csw(None))
  }

  private final class ExtractMdXmlResponse(cswResponse:Response[XmlValue]) extends Response[MetadataValue] {

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
object ImportStyleSheets extends Enumeration {
  type ImportStyleSheet = Value
  val GM03_V1 = Value("GM03-to-ISO19139CHE.xsl")
  val GM03_V2 = Value("GM03_2-to-ISO19139CHE.xsl")
  val ISO = Value("ISO19139-to-ISO19139CHE.xsl")
  val NONE = Value("_none_")
}
object ImportMetadata {
  def apply (fileName:String, data:String, styleSheet:ImportStyleSheets.ImportStyleSheet, validate:Boolean)(implicit config:GeonetConfig):ImportMetadata = {
     ImportMetadata(new File(fileName), styleSheet,validate,config.groupId)
  }

}
case class ImportMetadata(data:File, styleSheet:ImportStyleSheets.ImportStyleSheet, validate:Boolean, groupId:String)
  extends MultiPartFormRequest[Any,IdValue](
    "mef.import",
    IdValuesFactory.FromImportOrCreateResult,
    "insert_mode" -> new StringBody("1"),
    "file_type"-> new StringBody("single"),
    "mefFile" -> new FileBody(data,"application/xml"),
    "uuidAction"-> new StringBody("generateUUID"), //other options: nothing,
    "template"-> new StringBody("n"),
    "styleSheet"-> new StringBody(styleSheet.toString),
    "group"-> new StringBody(groupId),
    "category"-> new StringBody("_none_"),
    "validate"-> new StringBody(if(validate)"on" else "off")
  )
