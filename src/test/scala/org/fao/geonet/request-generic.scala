package org.fao.geonet

import java.io.OutputStream
import java.net.URLEncoder

case class XmlRequest(_serv:String, data:xml.Node) extends AbstractXmlRequest(_serv, XmlResponseFactory, data)
case class Form(_serv:String, form:(String,String)*) extends FormRequest(_serv, XmlResponseFactory, form:_*) {
   override def toString() = "Form("+_serv+")"
}

case class Get(_serv:String,params:(String,String)*) extends GetRequest(_serv, XmlResponseFactory, params:_*) {
  override def toString() = "Get("+_serv+")"
}
