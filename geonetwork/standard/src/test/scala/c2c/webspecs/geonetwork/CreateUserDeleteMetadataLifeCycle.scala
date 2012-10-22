package c2c.webspecs.geonetwork

import c2c.webspecs.UriResolver
import c2c.webspecs.ExecutionContext

class CreateUserDeleteMetadataLifeCycle(config: GeonetConfig) extends CreateAsNeededUserLifeCycle(config) {
  override def setup(implicit context: ExecutionContext, uriResolver: UriResolver) = {
    config.adminLogin.execute()(context, uriResolver)
    
    SetSequentialExecution(true).execute()
    SetUseNRTManagerReopenThread(true).execute()

    deleteAllMetadata(context, uriResolver)

    SetSequentialExecution(false).execute()
    SetUseNRTManagerReopenThread(false).execute()

    super.setup(context, uriResolver)
  }
  
  override def tearDown(implicit context: ExecutionContext, uriResolver:UriResolver) = {
    config.adminLogin.execute()(context, uriResolver)
    
    SetSequentialExecution(true).execute()
    SetUseNRTManagerReopenThread(true).execute()

    deleteAllMetadata(context, uriResolver)
    
    SetSequentialExecution(false).execute()
    SetUseNRTManagerReopenThread(false).execute()

  }
  
  def deleteAllMetadata(implicit executionContext:ExecutionContext, uriResolver:UriResolver) = {
    var loops = 5
    def search = XmlSearch().to(10000).fast(FastTypeEnum.fast).execute()
    while (search.value.records.nonEmpty && loops > 0) {
      (SelectAll then MetadataBatchDelete).execute()
      loops -= 1
    }

    val finalResults = search.value
    assert(finalResults.records.isEmpty, "Unable to delete all records")
  }

}