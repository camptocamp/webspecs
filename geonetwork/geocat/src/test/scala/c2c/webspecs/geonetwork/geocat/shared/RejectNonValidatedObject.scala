package c2c.webspecs
package geonetwork
package geocat.shared

import c2c.webspecs.geonetwork.geocat.shared.SharedObjectTypes.SharedObjectType
import c2c.webspecs.AbstractFormPostRequest

case class RejectNonValidatedObject(sharedObjectId: String,
  sharedType: SharedObjectType,
  rejectionMessage: String = "This is a test script rejecting your object, if this is a mistake please inform the system administrators")
  extends AbstractFormPostRequest[Any, IdValue](
    "reusable.reject",
    new DeletedSharedObjectIdFactory,
    P("id", sharedObjectId.toString),
    SP("testing" -> true),
    P("type", sharedType.toString),
    P("msg", rejectionMessage))