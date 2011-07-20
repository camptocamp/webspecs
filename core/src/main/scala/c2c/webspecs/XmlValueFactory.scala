package c2c.webspecs

object XmlValueFactory extends BasicValueFactory[XmlValue] {
  def createValue(rawValue:BasicHttpValue) = rawValue.toXmlValue
}
