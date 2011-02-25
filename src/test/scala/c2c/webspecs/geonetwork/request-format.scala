package c2c.webspecs
package geonetwork

case class Format(id:Int, name:String, version:String)
class FormatListValue(val basicValue:BasicHttpValue) extends XmlValue {
  private val Parser = """(.*?)\[(.*?)\]""".r
  lazy val formatList = withXml {xml=>
    (xml \\ "li").toList map {li =>
      val id = XLink.id(li).get.toInt
      val text = li.text.trim

      val (name,version) = text match {
        case Parser(name,version) => name -> version
        case name => name -> ""
      }
      val validated = (li \\ "@alt" text).trim.nonEmpty
      Format(id,name.trim,version.trim)
    }
  }
}
case class ListFormats(searchParam:String="")
  extends AbstractGetRequest[Any,FormatListValue](
    "xml.format.list",
    SelfValueFactory[Any,FormatListValue](),
    "name" -> searchParam)
with ValueFactory[Any,FormatListValue] {
  override def apply[A <: Any, B >: FormatListValue](request: Request[A, B], in: Any, rawValue: BasicHttpValue) = {
    new FormatListValue(rawValue)
  }
}

case class DeleteFormat(id:Int) extends AbstractGetRequest[Any,XmlValue]("format", XmlValueFactory, "action" -> "DELETE", "id" -> id)
