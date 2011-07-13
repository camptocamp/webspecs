package c2c.webspecs
package geonetwork
package csw

import scala.xml.NodeSeq
import ElementSetNames._

import ResultTypes._

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