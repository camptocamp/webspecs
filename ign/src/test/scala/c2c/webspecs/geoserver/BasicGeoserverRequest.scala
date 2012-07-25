package c2c.webspecs
package geoserver
import GeoserverRequests._
import scala.xml.Elem
import scala.xml.XML

object GeoserverRequests {
  def completeParams(version: String, service: String, op: String, base: Seq[(String, Any)]) =
      ("VERSION" -> version) +:
      ("SERVICE" -> service) +:
      ("REQUEST" -> op) +: base
  def url(workspace: Option[String], service: String) = workspace.map(ws => "geoserver/"+ ws + service) getOrElse "geoserver/"+service
}

abstract class WfsRequest(version: String, op: String, params: (String, Any)*) extends Request[Any, XmlValue] {
  def url = "wfs"
  def request: Request[Any, XmlValue]
  def apply(in: Any)(implicit context: ExecutionContext, uriResolver:UriResolver) = request.execute(in)
}



object GetWfsRequest {
  def apply(version: String, op: String, params: (String, Any)*): GetWfsRequest =
    new GetWfsRequest(None, version, op, params: _*)
  def apply(workspace: String, version: String, op: String, params: (String, Any)*): GetWfsRequest =
    new GetWfsRequest(Some(workspace), version, op, params: _*)
}
class GetWfsRequest(workspace: Option[String], version: String, op: String, params: (String, Any)*)
  extends AbstractGetRequest(url(workspace, "wfs"), XmlValueFactory, completeParams(version, "wfs", op, params).map(Param.stringMapping):_*)  

