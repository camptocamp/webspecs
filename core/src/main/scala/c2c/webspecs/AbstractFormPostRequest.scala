package c2c.webspecs

import scala.collection.JavaConverters._
import org.apache.http.client.methods.HttpPost
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity

abstract class AbstractFormPostRequest[-In, +Out](val uri:String,valueFactory:ValueFactory[In,Out],params:Param[In,String]*)
  extends AbstractRequest(valueFactory) {
  override def request(in:In, uriResolver:UriResolver) = {
    val post = new HttpPost(uriResolver(uri,Nil))
    Log.apply(Log.RequestForm, params mkString ("\t","\n\t",""))
    val formParams = params.map{p => new BasicNameValuePair(p.name,p.value(in))}.toList
    post.setEntity(new UrlEncodedFormEntity(formParams.asJava,"UTF-8"))
    post
  }
}
