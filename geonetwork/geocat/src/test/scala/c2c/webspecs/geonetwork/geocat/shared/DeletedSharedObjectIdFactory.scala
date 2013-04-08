package c2c.webspecs.geonetwork.geocat.shared

import c2c.webspecs.BasicValueFactory
import c2c.webspecs.BasicHttpValue
import c2c.webspecs.IdValue

class DeletedSharedObjectIdFactory extends BasicValueFactory[IdValue] {
  def createValue(rawValue: BasicHttpValue): IdValue = (rawValue.toXmlValue.getXml \\ "id" map {n => IdValue(n.text,rawValue)}).headOption.getOrElse(IdValue(null, rawValue))    
}