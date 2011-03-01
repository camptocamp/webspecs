package c2c.webspecs
package geonetwork

object DeleteResultingMetadata {
  def apply() = MetadataRequest.makeInputBasedMetadataRequest(id => DeleteMetadata(id))
}
case class DeleteMetadata(mdId:String) extends AbstractGetRequest(
  "metadata.delete",
  ExplicitIdValueFactory(mdId),
  "id" -> mdId)
object DeleteOwnedMetadata {
  def fromUserName(userName:String) = {
//    xx
    ()
  }
}
case class DeleteMetadataReport(deletedRecordIds:Map[String,BasicHttpResponse[IdValue]])

case class DeleteOwnedMetadata(userId:String) extends Request[Any,DeleteMetadataReport] {
  override def apply(in: Any)(implicit context: ExecutionContext) = {
    val props = PropertyIsLike("_owner",userId)
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