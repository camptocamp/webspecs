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
                                url:String="csw",
                                elementSetName:ElementSetName = full,
                                typeNames: List[String] = List("csw:Record"),
                                sortBy:Seq[SortBy] = Nil)
  extends AbstractXmlPostRequest[Any,XmlValue](url, XmlValueFactory) {

  val xmlData = CswXmlUtil.getRecordsXml(filter, resultType, outputSchema, startPosition, maxRecords, elementSetName,typeNames, sortBy)
  override def toString() = "CswGetRecordsRequest(<filter>,"+resultType+","+outputSchema+","+startPosition+","+maxRecords+","+elementSetName+",["+typeNames.mkString(",")+"],"+sortBy+")"
}
