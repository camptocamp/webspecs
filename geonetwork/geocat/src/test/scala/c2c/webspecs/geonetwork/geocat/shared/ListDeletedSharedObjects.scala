package c2c.webspecs
package geonetwork
package geocat
package shared

import c2c.webspecs.geonetwork.geocat.shared.SharedObjectTypes._

case object ListDeletedSharedObjects
  extends AbstractGetRequest[Any, SharedObjectList]("reusable.non_validated.list", new SharedObjectListFactory(deleted), P("type", deleted.toString))
