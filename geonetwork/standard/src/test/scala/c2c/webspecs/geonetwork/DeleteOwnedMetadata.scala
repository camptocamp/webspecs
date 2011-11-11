package c2c.webspecs
package geonetwork
import c2c.webspecs.geonetwork.csw.PropertyIsLike
import c2c.webspecs.geonetwork.csw.CswGetRecordsRequest

case object DeleteOwnedMetadata extends Request[UserRef,DeleteMetadataReport] {
  override def execute(in: UserRef)(implicit context: ExecutionContext, uriResolvers:UriResolver) = {
    val props = PropertyIsLike("_owner",in.userId)
    val csw = CswGetRecordsRequest(props.xml)
    csw.execute().value.withXml{ xml =>
      val ids = xml \\ "info" \ "id" map {_.text}
      val responses = ids.map {id => (id,DeleteMetadata.execute(Id(id)))}
      new Response[DeleteMetadataReport] {
        val basicValue = null
        val value = DeleteMetadataReport(Map(responses:_*))
      }
    }
  }
}