package c2c.webspecs.geonetwork.geocat.shared

import c2c.webspecs.AbstractGetRequest
import c2c.webspecs.XmlValueFactory
import c2c.webspecs.SP
import c2c.webspecs.P
import c2c.webspecs.XmlValue

case class DeleteSharedObject(sharedObjectId: String)
  extends AbstractGetRequest[Any, XmlValue](
    "reusable.delete",
    XmlValueFactory,
    SP("testing" -> true),
    P("id", sharedObjectId))