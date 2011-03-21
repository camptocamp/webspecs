package c2c.webspecs
package geonetwork

case class SortBy(key:String,asc:Boolean)
object OutputSchemas extends Enumeration {
  type OutputSchema = Value
  val GM03_2Record,own,IsoRecord = Value
  val DublinCore = Value("ogc")
  val CheRecord = Value("http://www.geocat.ch/2008/che")
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
                                outputSchema:OutputSchemas.OutputSchema=OutputSchemas.DublinCore,
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


