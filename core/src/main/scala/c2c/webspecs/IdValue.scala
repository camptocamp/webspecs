package c2c.webspecs

object IdValue {
  def apply(in:String,rawValue:BasicHttpValue) = new IdValue {
    protected def basicValue = rawValue
    def id = in
  }
}
trait IdValue extends XmlValue with Id

