package c2c.webspecs

case class StringPostRequest(uri:String, data:Any, contentType:String = "text/plain; charset=utf-8") 
	extends AbstractStringPostRequest[Any,XmlValue](uri,XmlValueFactory)
