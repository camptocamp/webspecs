package c2c.webspecs.geonetwork

import c2c.webspecs.UriResolver
import c2c.webspecs.ExecutionContext

class CreateUserDeleteMetadataLifeCycle(config: GeonetConfig) extends CreateAsNeededUserLifeCycle(config) {
  override def setup(implicit context: ExecutionContext, uriResolver: UriResolver) = {
    super.setup(context, uriResolver)
    config.adminLogin.execute()(context, uriResolver)
    deleteAllMetadata(context, uriResolver)
  }
  
  override def tearDown(implicit context: ExecutionContext, uriResolver:UriResolver) = {
    config.adminLogin.execute()(context, uriResolver)
    deleteAllMetadata(context, uriResolver)
  }
  
    def deleteAllMetadata(implicit executionContext:ExecutionContext, uriResolver:UriResolver) = {
    var loops = 5
    def search() = XmlSearch(Int.MaxValue).execute()
    while (search().value.records.nonEmpty && loops > 0) {
      (SelectAll then MetadataBatchDelete).execute()
      loops -= 1
    }

    assert(search().value.records.isEmpty, "Unable to delete all records")
  }

}