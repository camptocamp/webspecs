package c2c.webspecs
import org.apache.http.Header
import java.net.URI


object BasicHttpValue {
  def apply(executionResponse:ExecutionContext.Response):BasicHttpValue = {
    import executionResponse._
    import httpResponse._

    val contentLength = getEntity.getContentLength match {
      case x if x >= 0 => Some(x.toInt)
      case y => None
    }
    val responseCode = getStatusLine.getStatusCode
    val responseMessage = getStatusLine.getReasonPhrase

    val data = {
      try {
        import scalax.io.JavaConverters._
        val all = httpResponse.getEntity.getContent.asInput
        Right(all.byteArray)
      } catch {
        case e if responseCode > 400 => Left(new IllegalStateException("A response code "+responseCode+" was returned by server, message = "+responseMessage))
        case e: Throwable => Left(e)
      }
    }
    val headers = getAllHeaders.
        map{_.getName}.
        map{name => (name.toLowerCase,httpResponse.getHeaders(name).toList)}.toMap
    BasicHttpValue(
      data,
      responseCode,
      responseMessage,
      headers,
      contentLength,
      Option(getEntity.getContentType) map {_.getValue},
      Option(getEntity.getContentEncoding) map {_.getValue},
      finalHost,
      finalURL
    )
  }
}
case class BasicHttpValue(data:Either[Throwable,Array[Byte]],
                          responseCode:Int,
                          responseMessage:String,
                          allHeaders: Map[String, List[Header]],
                          contentLength:Option[Int],
                          contentType:Option[String],
                          contentEncoding:Option[String],
                          finalHost:String,
                          finalURL:Option[URI]) {
  private val self = this

  def firstHeader(fieldName:String) = allHeaders.get(fieldName) flatMap {_.headOption}
  def toTextValue = new TextValue {
    def basicValue = self
  }
  def toXmlValue = new XmlValue {
    def basicValue = self
  }
  def toZipValue = new ZipValue {
    def basicValue = self
  }
}
