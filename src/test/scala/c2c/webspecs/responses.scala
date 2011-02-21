package c2c.webspecs

import util.control.Exception._
import org.apache.http.{Header, HttpResponse}
import xml.NodeSeq

trait Response[+A] {
  def value:A
  def basicValue:BasicHttpValue
}

object EmptyResponse extends Response[Null] {
  def basicValue = new BasicHttpValue(
    Right(Array[Byte]()),
    200,
    "",
    Map[String,List[Header]](),
    Some(0),
    None,
    None
  )

  def value = null
}

class BasicHttpResponse[+A](val basicValue:BasicHttpValue,val value:A) extends Response[A]
trait ValueFactory[-In,+Out] {
  def apply[A <: In, B >: Out](request:Request[A,B],in:In,rawValue:BasicHttpValue):Out
}

object SelfValueFactory {
  def apply[In,Out]() = new ValueFactory[In,Out]{
    def apply[A <: In, B >: Out](request:Request[A,B],in:In,rawValue:BasicHttpValue):Out = {
      request.asInstanceOf[ValueFactory[In,Out]].apply(request,in,rawValue)
    }
  }
}
object XmlValueFactory extends ValueFactory[Any,XmlValue] {
  def apply[A <: Any, B >: XmlValue](request: Request[A, B], in: Any, rawValue: BasicHttpValue) = new XmlValue() {
    def basicValue = rawValue
  }
}
object BasicHttpValue {
  def apply(httpResponse:HttpResponse):BasicHttpValue = {
    import httpResponse._
    val contentLength = getEntity.getContentLength match {
      case x if x >= 0 => Some(x.toInt)
      case y => None
    }
    val responseCode = getStatusLine.getStatusCode
    val responseMessage = getStatusLine.getReasonPhrase

    val data = {
      try {
        import scalax.io.Input._
        val all = httpResponse.getEntity.getContent.asInput
        Right(all.byteArray)
      } catch {
        case e if responseCode > 400 => Left(new IllegalStateException("A response code "+responseCode+" was returned by server, message = "+responseMessage))
        case e => Left(e)
      }
    }
    val headers = getAllHeaders.
        map{_.getName}.
        map{name => (name,httpResponse.getHeaders(name).toList)}.toMap
    BasicHttpValue(
      data,
      responseCode,
      responseMessage,
      headers,
      contentLength,
      Option(getEntity.getContentType) map {_.getValue},
      Option(getEntity.getContentEncoding) map {_.getValue}
    )
  }
}
case class BasicHttpValue(data:Either[Throwable,Array[Byte]],
                          responseCode:Int,
                          responseMessage:String,
                          allHeaders: Map[String, List[Header]],
                          contentLength:Option[Int],
                          contentType:Option[String],
                          contentEncoding:Option[String])

object TextValue {
  def apply(basic:BasicHttpValue) = new TextValue {
    def basicValue = basic
  }
}
trait TextValue {
  def basicValue:BasicHttpValue
  lazy val text = basicValue.data match {
    case Right(data) =>
      allCatch[String].either { new String(data, "UTF8") }
    case Left(error) => Left(error)
  }
  def withText[R](f:String => R):R = text.fold(throw _, f)
}
object XmlValue {
  def apply(basic:BasicHttpValue) = new XmlValue {
    def basicValue = basic
  }
}
trait XmlValue extends TextValue {
  lazy val xml:Either[Throwable,NodeSeq] = text match {
    case Right(text) =>
      allCatch[NodeSeq].either {
        val xml = TagSoupFactoryAdapter.loadString(text)

        val error = xml \\ "ExceptionReport" \ "Exception" \ "ExceptionText"
        if(error.nonEmpty) {
          throw new IllegalStateException("Server response contained ExceptionReport: "+error.text.replace("&lt;","<").replace("&gt;",">"))
        }
        xml
      }
    case Left(error) => Left(error)
  }

  def withXml[R](f:NodeSeq => R):R = xml.fold(throw _, f)

}