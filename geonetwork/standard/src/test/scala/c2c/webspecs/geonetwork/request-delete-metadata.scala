package c2c.webspecs
package geonetwork

object DeleteResultingMetadata {
  def apply() = MetadataRequest.makeInputBasedMetadataRequest(id => DeleteMetadata(id))
}
case class DeleteMetadata(mdId:String) extends AbstractGetRequest(
  "metadata.delete",
  ExplicitIdValueFactory(mdId),
  "id" -> mdId)

case class DeleteMetadataReport(deletedRecordIds:Map[String,BasicHttpResponse[IdValue]])

case object DeleteOwnedMetadata extends Request[IdValue,DeleteMetadataReport] {
  override def apply(in: IdValue)(implicit context: ExecutionContext) = {
    val props = PropertyIsLike("_owner",in.id)
    val csw = CswGetRecordsRequest(props.xml)
    csw(None).value.withXml{ xml =>
      val ids = xml \\ "info" \ "id" map {_.text}
      val responses = ids.map {id => (id,DeleteMetadata(id)(None))}
      new Response[DeleteMetadataReport] {
        val basicValue = null
        val value = DeleteMetadataReport(Map(responses:_*))
      }
    }
  }
}