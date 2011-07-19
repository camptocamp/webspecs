package c2c.webspecs

case class GetRequest(uri:String, params:(String,Any)*) 
	extends AbstractGetRequest[Any,XmlValue](uri,XmlValueFactory,params.map(Param.stringMapping):_*) 
	with AnyRequest[XmlValue] 
