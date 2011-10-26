package c2c.webspecs
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import scala.xml.XML
import java.io.StringWriter


abstract class AbstractXmlPostRequest[-In, +Out](uri:String, valueFactory:ValueFactory[In,Out])
  extends AbstractStringPostRequest(uri, valueFactory) {
  val xmlData:xml.NodeSeq
  final lazy val data = {
    val out = new StringWriter()
    XML.write(out, xmlData.head, "UTF-8",true,null) 
    out
  }
  override val contentType = "text/xml; charset=utf-8" 
  override def toString() = "XmlRequest("+uri+")"
}
