package c2c.webspecs
import net.liftweb.json._

abstract class AbstractJsonPostRequest[-In, +Out](uri:String, valueFactory:ValueFactory[In,Out])
  extends AbstractStringPostRequest(uri, valueFactory) {
  val jsonData:JObject
  final lazy val data = compact(render(jsonData))
  override val contentType = "application/json; charset=utf-8" 
  override def toString() = "XmlRequest("+uri+")"
}
