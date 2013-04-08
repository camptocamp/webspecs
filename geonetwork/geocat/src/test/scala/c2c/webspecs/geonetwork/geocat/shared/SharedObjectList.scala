package c2c.webspecs.geonetwork.geocat.shared

import scala.collection.SeqProxy
import c2c.webspecs.BasicHttpValue
import c2c.webspecs.XmlValue

class SharedObjectList(val basicValue: BasicHttpValue,
  val self: List[SharedObject]) extends SeqProxy[SharedObject] with XmlValue
