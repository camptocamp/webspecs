package c2c.webspecs

import java.net.URL
import org.apache.http.entity.mime.content.ByteArrayBody
import scalax.file.Path
import scalax.io.Resource
import scalax.io.Codec
import java.util.UUID

object ResourceLoader {
  def loadData (resource:URL, uuid:UUID, fileName:String="") = {
    val string = Resource.fromURL(resource).slurpString(Codec.UTF8).replace("{uuid}",uuid.toString)

    val name =
      if (fileName.trim() == "") Path.fromString(resource.getFile).name
      else fileName

    (string, new ByteArrayBody(string.getBytes(Codec.UTF8.charSet),"application/xml",name))
  }
  def loadDataFromClassPath(file:String, cl:Class[_], uuid:UUID) = loadData(cl.getResource(file),uuid)

}