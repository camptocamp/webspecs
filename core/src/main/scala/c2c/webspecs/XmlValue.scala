package c2c.webspecs
import scala.xml.NodeSeq
import scala.xml.XML
import scala.util.control.Exception.allCatch
trait XmlValue extends TextValue {
  def parse(parser : String => NodeSeq):Either[Throwable,NodeSeq] = text match {
    case Right(text) =>
      allCatch[NodeSeq].either {
    	val xml = parser(text)
        val error = xml \\ "ExceptionReport" \ "Exception" \ "ExceptionText"
        if(error.nonEmpty) {
          val report = error.text.replace("&lt;", "<").replace("&gt;", ">") match {
            case "ogc" => xml \\ "ExceptionReport" \ "Exception"
            case text => text
          }

          throw new IllegalStateException("Server response contained ExceptionReport: "+report)
        }
        xml
      }
    case Left(error) => Left(error)
  }
  lazy val html = parse(TagSoupFactoryAdapter.loadString)
  lazy val xml  = parse(XML.loadString)
  
  private def throwAndPrint(t:Throwable) = {
    text.fold(_ => println("Failed to load text"),println) 
    
    throw t
  }
  def withXml[R](f:NodeSeq => R):R = xml.fold(throwAndPrint _, f)
  def withHtml[R](f:NodeSeq => R):R = html.fold(throwAndPrint _, f)
  def getXml = xml.fold(throw _, xml => xml)
  def getHtml = html.fold(throw _, xml => xml)
}
