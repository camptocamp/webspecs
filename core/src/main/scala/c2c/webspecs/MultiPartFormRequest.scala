package c2c.webspecs
import org.apache.http.entity.mime.content.ContentBody

case class MultiPartFormRequest(url:String, form:(Any,ContentBody)*)
  extends AbstractMultiPartFormRequest[Any,XmlValue](url,XmlValueFactory,form.map{
    case (s:Symbol, v) => P(s.name, v)
    case (k,v) => P(k.toString, v)
    }:_*)