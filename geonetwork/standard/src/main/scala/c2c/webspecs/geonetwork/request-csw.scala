package c2c.webspecs
package geonetwork

case class SortBy(key:String,asc:Boolean)
object OutputSchemas {
  abstract class  OutputSchema (val name:String) {
    override def toString: String = name
  }
  case object Record extends OutputSchema("csw:Record")
  case object IsoRecord extends OutputSchema("csw:IsoRecord")
  val DublinCore = Record
}
case class PropertyIsLike(name:String,literal:String) {
  val xml =
   <ogc:PropertyIsLike wildCard="*" singleChar="." escape="!">
    <ogc:PropertyName>{name}</ogc:PropertyName> <ogc:Literal>{literal}</ogc:Literal>
  </ogc:PropertyIsLike>
}
case class PropertyIsNotEqualTo(name:String,literal:String) {
  val xml =
   <ogc:PropertyIsNotEqualTo>
    <ogc:PropertyName>{name}</ogc:PropertyName> <ogc:Literal>{literal}</ogc:Literal>
  </ogc:PropertyIsNotEqualTo>
}
case class PropertyIsEqualTo(name:String,literal:String) {
  val xml =
   <ogc:PropertyIsEqualTo>
    <ogc:PropertyName>{name}</ogc:PropertyName> <ogc:Literal>{literal}</ogc:Literal>
  </ogc:PropertyIsEqualTo>
}
object ResultTypes extends Enumeration {
  type ResultType = Value
  val hits,results = Value
  val resultsWithSummary = Value("results_with_summary")
}
object ElementSetNames extends Enumeration {
  type ElementSetName = Value
  val full, brief, summary = Value
}

import ResultTypes._
import ElementSetNames._
import xml.{NodeSeq, Node}
import xml.Elem
import geonetwork.DomainProperties.DomainProperty
import geonetwork.DomainParameters.DomainParameter

object CswXmlUtil {
  def getByIdXml(fileId:String, outputSchema:OutputSchemas.OutputSchema) =
    <csw:GetRecordById xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" service="CSW" version="2.0.2" outputSchema={outputSchema.toString}>
      <csw:Id>{fileId}</csw:Id>
    </csw:GetRecordById>

  def getRecordsXml(filter:NodeSeq, resultType:ResultType, outputSchema:OutputSchemas.OutputSchema, startPosition:Int, maxRecords:Int, elementSetName:ElementSetName,sortBy:Option[SortBy]) = {
    <csw:GetRecords xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" service="CSW" version="2.0.2" resultType={resultType.toString}
                    startPosition={startPosition.toString} maxRecords={maxRecords.toString} outputSchema={outputSchema.toString}>
      <csw:Query typeNames="csw:Record">
        <csw:ElementSetName>{elementSetName}</csw:ElementSetName>
        {sortBy match {
          case None => Nil
          case Some(sortBy) =>
            <ogc:SortBy xmlns:ogc="http://www.opengis.net/ogc">
              <ogc:SortProperty>
                <ogc:PropertyName>{sortBy.key}</ogc:PropertyName>
                <ogc:SortOrder>{if(sortBy.asc)'A' else 'D'}</ogc:SortOrder>
              </ogc:SortProperty>
            </ogc:SortBy>
        }
      }
        {
        if(filter.nonEmpty){
          <csw:Constraint version="1.0.0">
            <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
              {filter}
            </ogc:Filter>
          </csw:Constraint>
        } else Nil
      }
      </csw:Query>
    </csw:GetRecords>
  }
}

case class CswGetRecordsRequest(filter:NodeSeq=Nil,
                                resultType:ResultType=hits,
                                outputSchema:OutputSchemas.OutputSchema=OutputSchemas.IsoRecord,
                                startPosition:Int=1,
                                maxRecords:Int=50,
                                elementSetName:ElementSetName = full,
                                sortBy:Option[SortBy] = None)
  extends AbstractXmlPostRequest[Any,XmlValue]("csw", XmlValueFactory) {

  def xmlData = CswXmlUtil.getRecordsXml( filter, resultType, outputSchema, startPosition, maxRecords, elementSetName,sortBy)
  override def toString() = "CswGetRecordsRequest(<filter>,"+resultType+","+outputSchema+","+startPosition+","+maxRecords+","+elementSetName+")"
}

case class CswGetByFileId(fileId:String, outputSchema:OutputSchemas.OutputSchema)
  extends AbstractXmlPostRequest[Any,XmlValue]("csw", XmlValueFactory) {

  def xmlData = CswXmlUtil.getByIdXml(fileId,outputSchema)
  override def toString() = "CswGetByFileId("+fileId+","+outputSchema+")"
}

trait DomainParam {val name:String}
object DomainProperties {
  abstract class DomainProperty(val name:String) extends DomainParam
  case object Title extends DomainProperty("Title")
  case object Subject extends DomainProperty("Subject")
  case object Owner extends DomainProperty("_owner")
  case object TopicCategory extends DomainProperty("topicCat")
  case object Type extends DomainProperty("Type")

}
object DomainParameters {
  abstract class DomainParameter(val name:String) extends DomainParam
  case object DescribeRecordOutputFormat extends DomainParameter("DescribeRecord.outputFormat")
  case object GetRecordsResultType extends DomainParameter("GetRecords.resultType")

}
case class CswGetDomain(firstParam:DomainParam, params:DomainParam*)
  extends AbstractXmlPostRequest[Any,XmlValue]("csw", XmlValueFactory) {

  val (propertyNames, parameterNames) = {
    val (propertyNames, parameterNames) = params.partition(_.isInstanceOf[DomainProperty])
    firstParam match {
      case p:DomainParameter =>
        (propertyNames, p +: parameterNames)
      case p:DomainProperty =>
        (p +: propertyNames, parameterNames)
    }
  }

  def xmlData =
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


