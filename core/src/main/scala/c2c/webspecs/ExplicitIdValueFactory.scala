package c2c.webspecs

case class ExplicitIdValueFactory(idVal:String) extends BasicValueFactory[IdValue]{
  def createValue(rawValue: BasicHttpValue) = new XmlValue with IdValue {
     val basicValue = rawValue
     val id = idVal
  }
}

