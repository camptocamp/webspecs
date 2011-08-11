package c2c.webspecs
package geonetwork

import java.io.{InputStreamReader, BufferedReader, InputStream, File}
import xml.{Node, Elem, NodeSeq}
import org.apache.http.entity.mime.content.{AbstractContentBody, ByteArrayBody, FileBody, StringBody}
import java.net.URL
import scalax.file.Path
import scalax.io.{Codec, Resource}
import MetadataViews.MetadataView
import java.util.UUID



object ImportMetadata {
  private def findGroupId (data:AbstractContentBody, styleSheet:ImportStyleSheets.ImportStyleSheet, validate:Boolean)(implicit config:GeonetConfig):ImportMetadata = {
     ImportMetadata(data, styleSheet,validate,config.groupId)
  }
  def defaults(
      uuid: UUID, 
      fileName: String, 
      validate:Boolean, 
      loaderRoot: Class[_],
      styleSheet:ImportStyleSheets.ImportStyleSheet = ImportStyleSheets.NONE)(implicit config: GeonetConfig):(String,ImportMetadata) = {
    defaultsWithReplacements(Map("{uuid}" -> uuid.toString), fileName, validate, loaderRoot, styleSheet)
  }
  def defaultsWithReplacements(replacements: Map[String,String], fileName: String, validate:Boolean, loaderRoot: Class[_],styleSheet:ImportStyleSheets.ImportStyleSheet = ImportStyleSheets.NONE)(implicit config: GeonetConfig):(String,ImportMetadata) = {
    val (original, content) = ResourceLoader.loadDataFromClassPath(fileName, loaderRoot, replacements)
    original -> findGroupId(content, styleSheet, validate)
  }

}
object UuidAction extends Enumeration {
  val generateUUID, overwrite, None = Value 
}
case class ImportMetadata(data:AbstractContentBody, styleSheet:ImportStyleSheets.ImportStyleSheet, validate:Boolean, groupId:String, uuidAction: UuidAction.Value = UuidAction.generateUUID)
  extends AbstractMultiPartFormRequest[Any,IdValue](
    "mef.import",
    IdValuesFactory.FromImportOrCreateResult,
    P("insert_mode",  new StringBody("1")),
    P("file_type",  new StringBody("single")),
    P("mefFile",  data),
    P("uuidAction",  new StringBody(uuidAction.toString())),
    P("template",  new StringBody("n")),
    P("styleSheet",  new StringBody(styleSheet.toString)),
    P("group",  new StringBody(groupId)),
    P("category",  new StringBody("_none_")),
    P("validate",  new StringBody(if(validate)"on" else "off"))
  )
