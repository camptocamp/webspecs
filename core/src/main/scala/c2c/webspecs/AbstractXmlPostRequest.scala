package c2c.webspecs
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import scala.xml.XML
import java.io.StringWriter


abstract class AbstractXmlPostRequest[-In, +Out](uri:String, valueFactory:ValueFactory[In,Out])
  extends AbstractRequest(valueFactory) {
  override def request(in:In) = {
    val out = new StringWriter()
    XML.write(out, xmlData.head, "UTF-8",true,null) 
    Log.apply(Log.RequestXml, out.toString())
    val post = new HttpPost(Config.resolveURI(uri))
    post.setEntity(new StringEntity(out.toString(),"UTF-8"));
    post.setHeader("Content-type", "text/xml; charset=utf-8");
    post
  }

  def xmlData:xml.NodeSeq

  override def toString() = "XmlRequest("+uri+")"
}
