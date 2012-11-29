package c2c.webspecs
import scala.xml.NodeSeq
import scala.xml.XML
import scala.util.control.Exception.allCatch
import scala.xml.Node
import java.util.zip.ZipInputStream
import resource.Resource
import java.io.ByteArrayInputStream
import java.util.zip.ZipEntry
import scalax.file.Path
trait ZipValue  {
  protected def basicValue:BasicHttpValue

  private def zipIn = 
    basicValue.data.fold(
        { t => throw t}, 
        {data =>resource.managed(new ZipInputStream(new ByteArrayInputStream(data)))})
  

  lazy val files = zipIn.acquireAndGet { in =>
    var list = Vector[ZipEntry]()
    var next = in.getNextEntry() 
    while(next != null) {
      list = list :+ next
      next = in.getNextEntry() 
    }
    list
  }
  
  lazy val fileNames = files.map(_.getName)
  
  def file(zipEntry: ZipEntry) = zipIn.acquireAndGet { in =>
    var next = in.getNextEntry()
    var data:Array[Byte] = null 
    while(next != null && data == null) {
      if(next.getName.equals(zipEntry.getName)) {
        val size = zipEntry.getSize.toInt
        data = new Array[Byte](size)
        var read = in.read(data)
        while(read < size-1) {
          read += in.read(data, read, size-read)
        }
      }
      next = in.getNextEntry() 
    }
    
    data
  }
}
