package c2c.webspecs
import org.apache.http.entity.mime.content.ContentBody

case class MultiPartFormRequest(url:String, form:(String,ContentBody)*)
  extends AbstractMultiPartFormRequest[Any,XmlValue](url,XmlValueFactory,form.map(p => P(p._1,p._2)):_*)