package org.fao.geonet


import collection.JavaConverters._
import xml.NodeSeq
import java.net.HttpURLConnection
import collection.mutable.ArrayBuffer
import java.io.{InputStream, BufferedInputStream}

trait ResponseFactory[R <: Response] {
  def wrapResponse(basicResponse:Response):R
}
trait TextResponse {
  self: Response =>
  def withText[R](f:String => R):R = text.fold(throw _, f)
}
trait XmlResponse extends TextResponse{
  self: Response =>
  def withXml[R](f:NodeSeq => R):R = xml.fold(throw _, f)
}

trait MetadataIdResponse {
  def id:Int
}
object ParamIdResponseFactory extends ResponseFactory[Response with XmlResponse with MetadataIdResponse] {
  def wrapResponse(basicResponse: Response) = new DecoratingResponse(basicResponse) with XmlResponse with MetadataIdResponse {
    val id = request.asInstanceOf[AbstractRequest[_]].queryParams("id").toInt  // TODO DecoratingResponse (in fact all responses) need the Request type type parameter
  }
}
object XmlResponseFactory extends ResponseFactory[Response with XmlResponse] {
  def wrapResponse(basicResponse: Response) = new DecoratingResponse(basicResponse) with XmlResponse
}
class SelfResponseFactory[R <: Response] extends ResponseFactory[R] {
  def wrapResponse(basicResponse: Response) = basicResponse.request.asInstanceOf[ResponseFactory[R]].wrapResponse(basicResponse)
}
object Response {
  def apply(request:Request, conn:HttpURLConnection)(implicit sideEffectFac:SideEffectFactory) = {
    new HttpResponse(request,conn)
  }
}
trait Response {
  def request:Request
  def effect:SideEffect
  def responseCode:Int
  def responseMessage:String
  def allHeaders:Map[String, Seq[String]]
  lazy val headers: Map[String, String] = allHeaders.map{case (key,value) => (key,value.head)}
  def contentLength:Option[Int]
  def text:Either[Throwable,String]
  def xml:Either[Throwable,NodeSeq]

  final def then[R<:Request](request:R):EffectRequest[R] = new EffectRequest[R](effect,request)
  final def trackThen[R<:Request](next:R):AccumulatingRequest[R] = new AccumulatingResponseRequest(this,next)
}
final class HttpResponse(val request:Request, conn:HttpURLConnection)(implicit sideEffectFac:SideEffectFactory) extends Response{

  lazy val effect = sideEffectFac(this)
  val responseCode = conn.getResponseCode
  val responseMessage = conn.getResponseMessage

  Log(Log.Connection, "ResponseCode: "+responseCode+" msg="+responseMessage+" for "+request)

  val allHeaders: Map[String, Seq[String]] = conn.getHeaderFields.asScala.map{case (key,list) => (key,list.asScala.toSeq)} toMap
  val contentLength = conn.getContentLength match {
    case x if x > 0 => Some(x)
    case _ => None
  }
  private val data:Either[Throwable,Array[Byte]] = {
    var in:InputStream = null
    try {
      in = new BufferedInputStream(conn.getInputStream)
      val all = ArrayBuffer[Byte]()
      val buf = new Array[Byte](contentLength getOrElse (1024 * 1024))
      var read = in.read(buf)
      all ++= buf.view.take(read)
      while(read>0) {
        read = in.read(buf)
        if(read > 0)
        all ++= buf.view.take(read)
      }
      Right(all.toArray)
    } catch {
      case e if responseCode > 400 => Left(new IllegalStateException("A response code "+responseCode+" was returned by server, message = "+responseMessage))
      case e => Left(e)
    } finally {
      if (in != null) in.close
    }
  }

  conn.disconnect

  private val catcher = util.control.Exception.catching(classOf[Throwable])
  lazy val text = data match {
    case Right(data) =>
      catcher.either[String] { new String(data, "UTF8") }
    case Left(error) => Left(error)
  }
  lazy val xml:Either[Throwable,NodeSeq] = text match {
    case Right(text) =>
      catcher.either[NodeSeq]{
        val xml = TagSoupFactoryAdapter.loadString(text)

        val error = xml \\ "ExceptionReport" \ "Exception" \ "ExceptionText"
        if(error.nonEmpty) {
          throw new IllegalStateException("Server response contained ExceptionReport: "+error.text.replace("&lt;","<").replace("&gt;",">"))
        }
        xml
      }
    case Left(error) => Left(error)
  }
}

abstract class DecoratingResponse[R <: Response](val request:Request,wrapped:R) extends Response {
  def this(response:R) = this(response.request, response)
  def xml = wrapped.xml
  def text = wrapped.text
  def contentLength = wrapped.contentLength
  def allHeaders = wrapped.allHeaders
  def responseMessage = wrapped.responseMessage
  def responseCode = wrapped.responseCode
  def effect = wrapped.effect
}

class EmptyResponse(val request:Request, val effect:SideEffect = NoEffect) extends Response {
  val xml : Left[Throwable, NodeSeq] = Left(new AssertionError("No Data"))
  val text : Left[Throwable, String] = Left(new AssertionError("No Data"))
  val contentLength : Option[Int] = None
  val allHeaders:Map[String, Seq[String]] = Map.empty
  val responseCode = 200
  val responseMessage = ""
}
object AccumulatedResponse {
  def apply(previous:Response, mostRecent:Response):AccumulatedResponse = {
    (previous,mostRecent) match {
      case (previous:AccumulatedResponse, mostRecent) =>
        val filtered = previous.responses ::: previous.last :: Nil filterNot{_.isInstanceOf[EmptyResponse]}
        new AccumulatedResponse(filtered, mostRecent)
      case (previous, mostRecent) =>
        new AccumulatedResponse(List(previous),mostRecent)
    }
  }
  def unapplySeq(response:AccumulatedResponse):Option[Seq[Response]] = Some(response.responses ::: response.last :: Nil)
  object Last {
    def unapply(response:AccumulatedResponse):Option[Response] = Some(response.last)
  }
}
class AccumulatedResponse(val responses:List[Response], val last:Response) extends DecoratingResponse(last.request, last) {
  def apply (responseIndex:Int) = responses(responseIndex)
  def dontTrack(next:Response) = {
    new AccumulatedResponse(responses,next)
  }

  override def toString = "AccumulatedResponse("+responses.mkString(",")+")"
}
