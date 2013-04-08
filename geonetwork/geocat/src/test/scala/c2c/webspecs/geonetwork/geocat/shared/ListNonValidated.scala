package c2c.webspecs
package geonetwork
package geocat
package shared

import c2c.webspecs.geonetwork.geocat.shared.SharedObjectTypes._

case class ListNonValidated(sharedType: SharedObjectType)
  extends AbstractGetRequest[Any, SharedObjectList]("reusable.non_validated.list", new SharedObjectListFactory(sharedType), P("type", sharedType.toString))
