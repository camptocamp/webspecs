package c2c.webspecs
import net.liftweb.json.JObject
case class JsonPostRequest(uri:String, jsonData:JObject) 
	extends AbstractJsonPostRequest[Any,XmlValue](uri,XmlValueFactory)
