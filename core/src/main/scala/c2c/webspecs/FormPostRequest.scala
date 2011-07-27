package c2c.webspecs

case class FormPostRequest(override val uri:String, form:(String,Any)*) 
	extends AbstractFormPostRequest[Any,XmlValue](uri,XmlValueFactory,form.map(Param.stringMapping):_*)
