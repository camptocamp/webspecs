package c2c.webspecs.geonetwork

import c2c.webspecs.ExecutionContext

/**
 * Geonetwork specification trait that deletes all metadata from the catalog then
 * adds the sample data, and after execution deletes the metadata again
 *
 * User: jeichar
 * Date: 1/19/12
 * Time: 6:22 PM
 */
trait SampleDataGeonetworkSpecification extends GeonetworkSpecification {
  override def extraSetup(setupContext: ExecutionContext) = {
    super.extraSetup(setupContext)
    config.adminLogin.execute()(setupContext,uriResolver)
    deleteAllMetadata(false)(setupContext)
    AddAllSampleData.assertPassed()(setupContext,uriResolver)
    UserLogin.execute()(setupContext,uriResolver)
  }

  override def extraTeardown(tearDownContext: ExecutionContext) = {
    deleteAllMetadata(true)(tearDownContext)
    super.extraTeardown(tearDownContext)
  }
}