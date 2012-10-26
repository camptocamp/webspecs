package c2c.webspecs
import scala.xml.NodeSeq
import scala.xml.XML
import scala.util.control.Exception.allCatch
import scala.xml.Node
trait XmlValue extends TextValue {
  def parse(parser : String => NodeSeq):Either[Throwable,NodeSeq] = text match {
    case Right(text) =>
      allCatch[NodeSeq].either {
    	val xml = parser(text)
        val error = xml \\ "ExceptionReport" \ "Exception" \ "ExceptionText"
        if(error.nonEmpty) {
          val report = error.text.replace("&lt;", "<").replace("&gt;", ">") match {
            case "ogc" => xml.toString
            case text => text
          }

          throw new IllegalStateException("Server response contained ExceptionReport: "+report)
        }
        xml
      }
    case Left(error) => Left(error)
  }
  lazy val html = parse(TagSoupFactoryAdapter.loadString)
  lazy val xml  = parse( xmlString => {
    val modifiedString = if(xmlString.trim().startsWith("<!DOCTYPE")) {
      xmlString.trim.dropWhile(_ != '>').drop(1)
    } else {
      xmlString
    }
	XML.loadString(modifiedString)
  })
  
  private def throwAndPrint(t:Throwable) = {
    text.fold(_ => println("Failed to load text"),println) 
    
    throw t
  }
  def withXml[R](f:Node => R):R = xml.fold(throwAndPrint _, n => f(n.head))
  def withHtml[R](f:NodeSeq => R):R = html.fold(throwAndPrint _, f)
  def getXml:Node = xml.fold(_ => getHtml.head, xml => xml.head)
  def getHtml = html.fold(throw _, xml => xml)
}
