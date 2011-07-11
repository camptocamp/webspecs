package c2c.webspecs
package geonetwork
package geocat

case class Format(id:Int, name:String, version:String, validated:Boolean = false)

/**
 * Lists all formats that match the input pattern.
 */
object ListFormats
  extends AbstractGetRequest[String,List[Format]](
    "xml.format.list!",
    SelfValueFactory[String,List[Format]],
    IdP("name"))
  with BasicValueFactory[List[Format]] {
  def apply() = ListFormats.setIn("")

  override def createValue(rawValue: BasicHttpValue) = {
    rawValue.toXmlValue.withXml {xml=>
      (xml \\ "record").toList map {record =>
        val id = record \ "id" text
        val name = record \ "name" text
        val version = record \ "version" text
        val validated = (record \ "validated" text).trim.nonEmpty

        Format(id.toInt,name.trim,version.trim,validated)
      }
    }
  }
}

/**
 * Get xml for the format with the id passed as input
 */
object GetFormat
  extends Request[Int,Format] {
  def apply(in: Int)(implicit context: ExecutionContext): Response[Format] = {
    val response = ListFormats.setIn("")(None)
    response.map {_.find(_.id == in).get}
  }
}

/**
 * Ad a new format with given name and version.
 */
case class AddFormat(name:String,version:String)
  extends AbstractGetRequest[Any,XmlValue]("format", XmlValueFactory,
    P("action", "ADD"),
    P("name",name),
    P("version",version))


/**
 * Delete format. input is the id of the format to delete
 *
 */
object DeleteFormat
  extends AbstractGetRequest[Int,XmlValue]( "format", XmlValueFactory,
    P("action", "DELETE"),
    IdP("id")) {

  def apply(name:String,version:String):Request[Any,XmlValue] =
    {
      val FindFormat = ListFormats.setIn(name).map(allFormats =>
        allFormats.find(_.version == version).get.id
      )
      FindFormat then DeleteFormat
    }
}