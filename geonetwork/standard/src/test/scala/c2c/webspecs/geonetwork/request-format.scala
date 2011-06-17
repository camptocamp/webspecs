package c2c.webspecs
package geonetwork

case class Format(id:Int, name:String, version:String)

/**
 * Lists all formats that match the input pattern.
 */
object ListFormats
  extends AbstractGetRequest[String,List[Format]](
    "xml.format.list",
    SelfValueFactory[String,List[Format]],
    IdP("name"))
  with BasicValueFactory[List[Format]] {

  override def createValue(rawValue: BasicHttpValue) = {
    val Parser = """(.*?)\[(.*?)\]""".r
    rawValue.toXmlValue.withXml {xml=>
      (xml \\ "li").toList map {li =>
        val id = XLink.id(li).get.toInt
        val text = li.text.trim

        val (name,version) = text match {
          case Parser(name,version) => name -> version
          case name => name -> ""
        }
        Format(id,name.trim,version.trim)
      }
    }
  }
}

/**
 * Delete format. input is the id of the format to delete
 *
 */
object DeleteFormat extends AbstractGetRequest[String,XmlValue]("format", XmlValueFactory, P("action", "DELETE"), IdP("id")) {
  def apply(name:String,version:String):Request[Any,XmlValue] = {
    throw new RuntimeException("not implemented yet")
    //ListFormats.map(allFormats => allFormats.headOption.map(format.id)) valueThen { id => }
    }
}
