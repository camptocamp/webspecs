package c2c.webspecs
package geonetwork
package csw
import DomainProperties._
import DomainParameters._
import scala.xml.NodeSeq

case class CswGetDomain(firstParam:DomainParam, params:DomainParam*)
  extends AbstractXmlPostRequest[Any,XmlValue]("csw", XmlValueFactory) {

  private[this] val (propertyNames, parameterNames) = {
    val (propertyNames, parameterNames) = params.partition(_.isInstanceOf[DomainProperty])
    firstParam match {
      case p:DomainParameter =>
        (propertyNames, p +: parameterNames)
      case p:DomainProperty =>
        (p +: propertyNames, parameterNames)
    }
  }

  val xmlData =
    <csw:GetDomain xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" service="CSW">
      {
        if(propertyNames.nonEmpty) {
          <csw:PropertyName>{propertyNames.map{_.name}.mkString(",")}</csw:PropertyName>
        } else {
          NodeSeq.Empty
        }
      }
      {
        if(parameterNames.nonEmpty) {
          <csw:ParameterName>{parameterNames.map{_.name}.mkString(",")}</csw:ParameterName>
        } else {
          NodeSeq.Empty
        }
      }
    </csw:GetDomain>
}
