package c2c.webspecs.geonetwork.geocat.shared

import c2c.webspecs.BasicHttpValue
import c2c.webspecs.AbstractMultiPartFormRequest
import c2c.webspecs.BasicValueFactory
import scala.xml.NodeSeq
import scala.xml.Node
import c2c.webspecs.SelfValueFactory
import c2c.webspecs.P
import org.apache.http.entity.mime.content.StringBody
import java.nio.charset.Charset

case class ProcessSharedObject(xmlData: Node, addOnly: Boolean = false, defaultLang: String = "EN")
  extends AbstractMultiPartFormRequest[Any, NodeSeq](
    "reusable.object.process",
    SelfValueFactory[Any, NodeSeq](),
    P("xml", new StringBody(xmlData.toString, "text/xml", Charset.forName("UTF-8"))),
    P("addOnly", new StringBody(addOnly.toString)),
    P(defaultLang, new StringBody("EN")))
  with BasicValueFactory[NodeSeq] {
  def createValue(rawValue: BasicHttpValue): NodeSeq = rawValue.toXmlValue.withXml(x => x)
}