package c2c.webspecs
package geonetwork
package geocat.shared

import c2c.webspecs.AbstractGetRequest
import c2c.webspecs.geonetwork.geocat.shared.SharedObjectTypes.SharedObjectType

/**
 * Find the metadata that contain a reference to the shared object
 */
case class ListReferencingMetadata(sharedObjectId: String, sharedType: SharedObjectType)
  extends AbstractGetRequest[Any, ReferencingMetadataList](
    "reusable.references",
    ReferencingMetadataListFactory,
    P("id", sharedObjectId.toString),
    P("type", sharedType.toString))
