package c2c.webspecs
package geonetwork
package csw

import ResultTypes._

case class CswGetRecordById(
    fileId:String, 
    outputSchema:OutputSchemas.OutputSchema = OutputSchemas.IsoRecord,
    resultType:ResultType=results,
    url:String="csw",
    extraIds:Seq[String] = Seq.empty)
  extends AbstractXmlPostRequest[Any,XmlValue](url, XmlValueFactory) {
  val xmlData = CswXmlUtil.getByIdXml(fileId+:extraIds,resultType,outputSchema)
  override def toString() = "CswGetByFileId("+fileId+","+outputSchema+")"
}

