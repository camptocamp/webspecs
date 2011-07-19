package c2c.webspecs
package geonetwork
package csw

import ResultTypes._

case class CswGetByFileId(
    fileId:String, 
    outputSchema:OutputSchemas.OutputSchema,
    resultType:ResultType=results)
  extends AbstractXmlPostRequest[Any,XmlValue]("csw", XmlValueFactory) {

  def xmlData = CswXmlUtil.getByIdXml(fileId,resultType,outputSchema)
  override def toString() = "CswGetByFileId("+fileId+","+outputSchema+")"
}

