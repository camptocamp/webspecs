package c2c.webspecs
import scala.xml.NodeSeq
import scala.util.control.Exception.allCatch
trait XmlValue extends TextValue {
  lazy val xml:Either[Throwable,NodeSeq] = text match {
    case Right(text) =>
      allCatch[NodeSeq].either {
        val xml = TagSoupFactoryAdapter.loadString(text)

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

  def withXml[R](f:NodeSeq => R):R = xml.fold(throw _, f)
}

