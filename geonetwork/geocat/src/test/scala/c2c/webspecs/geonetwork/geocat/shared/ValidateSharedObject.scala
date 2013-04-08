package c2c.webspecs.geonetwork.geocat.shared

import c2c.webspecs.AbstractGetRequest
import c2c.webspecs.ExplicitIdValueFactory
import c2c.webspecs.SP
import c2c.webspecs.geonetwork.geocat.shared.SharedObjectTypes.SharedObjectType
import c2c.webspecs.IdValue
import c2c.webspecs.P

object ValidateSharedObject {
  def apply(obj: SharedObject):ValidateSharedObject = new ValidateSharedObject(obj.id, obj.objType)
}

case class ValidateSharedObject(sharedObjectId: String, sharedType: SharedObjectType)
  extends AbstractGetRequest[Any, IdValue](
    "reusable.validate",
    ExplicitIdValueFactory(sharedObjectId),
    P("id", sharedObjectId.toString),
    SP("testing" -> true),
    P("type", sharedType.toString))