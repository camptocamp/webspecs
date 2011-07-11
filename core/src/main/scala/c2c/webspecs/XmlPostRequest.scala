package c2c.webspecs

case class XmlPostRequest(uri:String, xmlData:xml.NodeSeq) extends AbstractXmlPostRequest[Any,XmlValue](uri,XmlValueFactory)
