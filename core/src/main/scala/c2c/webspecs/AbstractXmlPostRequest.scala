package c2c.webspecs
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity


abstract class AbstractXmlPostRequest[-In, +Out](uri:String, valueFactory:ValueFactory[In,Out])
  extends AbstractRequest(valueFactory) {
  override def request(in:In) = {
    Log.apply(Log.RequestXml, xmlData.toString)
    val post = new HttpPost(Config.resolveURI(uri))
    post.setEntity(new StringEntity(xmlData.toString));
    post.setHeader("Content-type", "text/xml");
    post
  }

  def xmlData:xml.NodeSeq

  override def toString() = "XmlRequest("+uri+")"
}
