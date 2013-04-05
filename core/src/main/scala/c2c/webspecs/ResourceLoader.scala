package c2c.webspecs

import java.net.URL
import org.apache.http.entity.mime.content.ByteArrayBody
import scalax.file.Path
import scalax.io.Resource
import scalax.io.Codec
import java.util.UUID
import org.apache.http.entity.mime.content.InputStreamBody

object ResourceLoader {
  def loadData(resource: URL, replacements: Map[String, String], fileName: String = "") = {
    require(resource != null, "resource is null." + fileName)
    val string = replacements.foldLeft(Resource.fromURL(resource).string) {
      case (string, (key, replacement)) => string.replace(key, replacement)
    }
    val name =
      if (fileName.trim() == "") Path.fromString(resource.getFile).name
      else fileName

    (string, new ByteArrayBody(string.getBytes(Codec.UTF8.charSet), "application/xml", name))
  }

  def loadDataFromClassPath(file: String, cl: Class[_], replacements: Map[String, String]) = {
    require(cl.getResource(file) != null, file + " cannot be loaded from the classpath of " + cl.getName + ". Perhaps a / is required at the start of the path?")
    loadData(cl.getResource(file), replacements)
  }

  def loadDataFromClassPath(file: String, cl: Class[_], uuid: UUID) = {
    require(cl.getResource(file) != null, file + " cannot be loaded from the classpath of " + cl.getName + ". Perhaps a / is required at the start of the path?")
    loadData(cl.getResource(file), Map("{uuid}" -> uuid.toString))
  }
  
  def loadImageFromClassPath(file:String, cl: Class[_]) = {
    val path = Path.fromString(file)
    val extension = path.extension getOrElse "png"
    new InputStreamBody(cl.getResourceAsStream(file), "image/"+extension, path.name)
  }
  
  def loadImageFromClassPathIntoMemory(file:String, cl: Class[_]) = {
    val path = Path.fromString(file)
    val extension = path.extension getOrElse "png"
    new ByteArrayBody(Resource.fromURL(cl.getResource(file)).byteArray, "image/"+extension, path.name)
  }

}