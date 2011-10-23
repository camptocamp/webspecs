package c2c.webspecs
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import scala.xml.XML
import java.io.StringWriter


abstract class AbstractStringPostRequest[-In, +Out](uri:String, valueFactory:ValueFactory[In,Out])
  extends AbstractRequest(valueFactory) {
  override def request(in:In) = {
    val out = data
    Log.apply(Log.RequestXml, data.toString())
    val post = new HttpPost(Config.resolveURI(uri))
    post.setEntity(new StringEntity(out.toString(),"UTF-8"));
    post.setHeader("Content-type", contentType);
    post
  }
  val contentType:String
  val data:Any

  override def toString() = "XmlRequest("+uri+")"
}
