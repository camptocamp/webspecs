package c2c.webspecs
package geonetwork

case object DeleteMetadata extends AbstractGetRequest[Id,IdValue](
  "metadata.delete",
  InputTransformerValueFactory[Id,IdValue]((in,raw) => IdValue(in.id,raw) ),
  InP("id", id => id.id))

case class DeleteMetadataReport(deletedRecordIds:Map[String,BasicHttpResponse[IdValue]])

case object DeleteOwnedMetadata extends Request[UserRef,DeleteMetadataReport] {
  override def apply(in: UserRef)(implicit context: ExecutionContext) = {
    val props = PropertyIsLike("_owner",in.userId)
    val csw = CswGetRecordsRequest(props.xml)
    csw(None).value.withXml{ xml =>
      val ids = xml \\ "info" \ "id" map {_.text}
      val responses = ids.map {id => (id,DeleteMetadata(Id(id)))}
      new Response[DeleteMetadataReport] {
        val basicValue = null
        val value = DeleteMetadataReport(Map(responses:_*))
      }
    }
  }
}