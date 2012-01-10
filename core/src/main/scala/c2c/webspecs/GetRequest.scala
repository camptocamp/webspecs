package c2c.webspecs

case class GetRequest(uri:String, params:(Any,Any)*) 
	extends AbstractGetRequest[Any,XmlValue](uri,XmlValueFactory,params.map(Param.stringMapping):_*)
