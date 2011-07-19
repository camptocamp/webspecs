package c2c.webspecs
package geonetwork
package csw

import ResultTypes._

case class CswGetByFileId(
    fileId:String, 
    outputSchema:OutputSchemas.OutputSchema,
    resultType:ResultType=results,
    url:String="csw")
  extends AbstractXmlPostRequest[Any,XmlValue](url, XmlValueFactory) {

  def xmlData = CswXmlUtil.getByIdXml(fileId,resultType,outputSchema)
  override def toString() = "CswGetByFileId("+fileId+","+outputSchema+")"
}

