package c2c.webspecs
package geonetwork

import java.io.{InputStreamReader, BufferedReader, InputStream, File}
import xml.{Node, Elem, NodeSeq}
import org.apache.http.entity.mime.content.{AbstractContentBody, ByteArrayBody, FileBody, StringBody}
import java.net.URL
import scalax.file.Path
import scalax.io.{Codec, Resource}

import MetadataViews.MetadataView



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
