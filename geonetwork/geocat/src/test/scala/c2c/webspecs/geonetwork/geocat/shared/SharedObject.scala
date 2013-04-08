package c2c.webspecs.geonetwork.geocat.shared

import c2c.webspecs.geonetwork.geocat.shared.SharedObjectTypes.SharedObjectType

case class SharedObject(id: String,
  url: Option[String],
  description: String,
  objType: SharedObjectType)
