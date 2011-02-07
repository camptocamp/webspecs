package org.fao.geonet

case class Format(id:Int, name:String, version:String)
class ListFormatResponse(response:Response) extends DecoratingResponse(response) with XmlResponse {
  private val Parser = """(.*?)\[(.*?)\]""".r
  lazy val list = withXml {xml=>
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
  extends GetRequest("xml.format.list", new SelfResponseFactory[ListFormatResponse](), "name" -> searchParam)
with ResponseFactory[ListFormatResponse] {
  def wrapResponse(basicResponse: Response) = new ListFormatResponse(basicResponse)
}

case class DeleteFormat(id:Int) extends GetRequest("format", XmlResponseFactory, "action" -> "DELETE", "id" -> id.toString)
