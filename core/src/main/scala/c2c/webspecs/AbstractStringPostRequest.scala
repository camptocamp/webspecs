package c2c.webspecs
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity


abstract class AbstractStringPostRequest[-In, +Out](uri:String, valueFactory:ValueFactory[In,Out])
  extends AbstractRequest(valueFactory) {
  override def request(in:In, uriResolver:UriResolver) = {
    val out = data
    Log.apply(Log.RequestXml, data.toString())
    val post = new HttpPost(uriResolver(uri, Nil))
    post.setEntity(new StringEntity(out.toString(),"UTF-8"));
    post.setHeader("Content-type", contentType);
    post
  }
  val contentType:String
  val data:Any

  override def toString() = "XmlRequest("+uri+")"
}
