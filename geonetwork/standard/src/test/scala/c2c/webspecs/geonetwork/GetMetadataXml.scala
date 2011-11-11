package c2c.webspecs
package geonetwork

import csw._
import scala.xml.Elem

case class GetMetadataXml(schema:OutputSchemas.OutputSchema = OutputSchemas.IsoRecord) extends Request[Id,MetadataValue] {
  private val getRequest = this
  private def csw(id:String) = CswGetRecordsRequest(PropertyIsEqualTo("geonetworkId",id).xml,ResultTypes.results,schema)

  override def execute(in: Id)(implicit context: ExecutionContext, uriResolver:UriResolver):Response[MetadataValue] = {
    val metadataXml: BasicHttpResponse[XmlValue] = csw(in.id).execute()
    new ExtractMdXmlResponse(in.id,metadataXml)
  }

  private final class ExtractMdXmlResponse(mdId:String, cswResponse:Response[XmlValue]) extends Response[MetadataValue] {

    def basicValue = cswResponse.basicValue
    val value = new MetadataValue {
      def basicValue = cswResponse.basicValue
      val id = mdId
      override lazy val xml = cswResponse.value.xml match {
        case Right(xmlData) =>
          util.control.Exception.catching(classOf[Throwable]) either {
            val resultsElem = (xmlData \\ "SearchResults")
            assert(resultsElem.size > 0, "CswGetRecords returned no SearchResults Elem metadataId: "+mdId)
            assert(resultsElem.size == 1, "CswGetRecords returned too many SearchResults Elements ("+resultsElem.size+") for metadataId: "+mdId)
            val results = resultsElem.head.child.collect{case n:Elem => n}
            assert(results.size > 0, "CswGetRecords did not return any results for metadataId: "+mdId)
            assert(results.size == 1, "CswGetRecords returned too many results ("+results.size+") for metadataId: "+mdId)
            results.head
          }
        case left => left
      }

      override lazy val text = xml match {
        case Left(error) => Left(error)
        case Right(xml) => Right(xml.toString)
      }
    }
  }
}