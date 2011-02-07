package org.fao.geonet

import collection.JavaConverters._
import java.io.{IOException, OutputStream, BufferedOutputStream}
import java.net.URLEncoder

object RequestMethods extends Enumeration {
  type Method = Value
  val POST = Value("POST")
  val GET = Value("GET")
}

import RequestMethods._
object Request {
  def chain(requests:TraversableOnce[Request]) = ((NoRequest:Request) /: requests) {
    case (req,next) => req then next
  }
}
trait Request extends Function0[Response]{
  def assertPassed() = apply{
    case response if response.responseCode > 200 => throw new AssertionError(toString+" did not complete correctly, reponseCode="+response.responseCode)
    case response => response
  }
  def apply() = exec(None)
  def apply[R](f:Response => R):R = f(exec(None))
  def exec(sideEffect:Option[SideEffect]):Response
  final def then[R1<:Request](next:R1):ChainedRequest[R1] = new ChainedRequest(this,_ => next)
  final def then[R1<:Request](next:Response => R1):ChainedRequest[R1] = new ChainedRequest(this,next)
  final def trackThen[R1<:Request](next:Response => R1):AccumulatingRequest[R1] = new AccumulatingRequestImpl(this,next)
  final def trackThen[R1<:Request](next:R1):AccumulatingRequest[R1] = new AccumulatingRequestImpl(this,_ => next)
}
abstract class AbstractRequest[RF <: Response](responseFactory:ResponseFactory[RF], service:String,contentType:String,method:Method=POST, val queryParams:Map[String,String] = Map.empty, headers:Map[String,String] = Map.empty) extends Request{
  def createSideEffect():SideEffectFactory = JSessionEffectFactory
  def writeData(out:OutputStream) = ()

  final def exec(sideEffect:Option[SideEffect]) = {
    val conn = Config.connect(service,queryParams)
    sideEffect.foreach{_.apply(conn)}
    conn.addRequestProperty("Content-Type",contentType)
    headers foreach {case (key,value) => conn.addRequestProperty(key,value)}

    Log(Log.Connection, "headers:"+ (conn.getRequestProperties.asScala mkString ", "))

    if(method == POST) {
      conn.setDoOutput(true)
      val out = new BufferedOutputStream(conn.getOutputStream)
      try {
        writeData(out)
      } finally {
        out.close
      }
    }
    val newEffectFactory = createSideEffect
    val finalFactory = if(sideEffect.isDefined) {
      new ChainedEffectFactory(sideEffect.get, newEffectFactory)
    } else {
      newEffectFactory
    }

    responseFactory.wrapResponse(Response(this,conn)(finalFactory))
  }
}
abstract class AbstractXmlRequest[RF <: Response](_serv:String, responseFactory:ResponseFactory[RF], data:xml.NodeSeq)
  extends AbstractRequest(responseFactory, _serv,"application/xml") {

  override def writeData(out: OutputStream) = {
    Log(Log.RequestXml, data.toString)
    out.write(data.toString.getBytes)
  }

  override def toString() = "XmlRequest("+_serv+")"
}
abstract class GetRequest[RS <: Response](_serv:String, responseFactory:ResponseFactory[RS], params:(String,Any)*)
  extends AbstractRequest(responseFactory,_serv, "text/html",GET,Map(params:_*).map{case (key,value) => (key,value.toString)})
abstract class FormRequest[RS <: Response](_serv:String, responseFactory:ResponseFactory[RS], form:(String,Any)*)
  extends AbstractRequest(responseFactory, _serv,"application/x-www-form-urlencoded") {
  def enc(s:String) = URLEncoder.encode(s, "UTF-8")
  override def writeData(out: OutputStream) = {
    Log(Log.RequestForm, "Form Params: \n"+(form map {e => e._1+"->"+e._2} mkString ("\t","\n\t","")))
    val values = form.map{case (key,value) => enc(key)+"="+enc(value.toString)} mkString "&"
    out.write(values.getBytes("UTF-8"))
  }
   override def toString() = "FormRequest("+_serv+")"
}
class ChainedRequest[R1 <: Request](current:Request , next:(Response) => R1) extends Request {
  def exec(sideEffect: Option[SideEffect]) = {
    def execNext(response:Response) = next(response).exec(Some(response.effect))
    current.exec(sideEffect) match {
      case response if response.responseCode > 400 =>
        throw new IOException("Executing "+response.request+" failed with a "+response.responseCode+" responseCode, message = "+response.responseMessage+"\ntext:\n"+response.text)
      case response:AccumulatedResponse =>
        response.dontTrack(execNext(response))
      case response =>
        execNext(response)
    }
  }

  override def toString() = current+" then "+next
}

trait AccumulatingRequest[R1<:Request] extends Request {
  def exec(sideEffect: Option[SideEffect]): AccumulatedResponse
  def accumulated[R](f: AccumulatedResponse => R) = f(exec(None))
  override def apply() = exec(None)
}

private class AccumulatingRequestImpl[R1<:Request](current:Request , next:(Response) => R1) extends AccumulatingRequest[R1] {
  def exec(sideEffect: Option[SideEffect]):AccumulatedResponse = {
    current.exec(sideEffect) match {
      case response if response.responseCode > 400 =>
        throw new IOException("Executing "+response.request+" failed with a "+response.responseCode+" responseCode: "+response.responseMessage)
      case previousResponse =>
        val latestResponse = next(previousResponse).exec(Some(previousResponse.effect))
        AccumulatedResponse(previousResponse, latestResponse)
    }
  }

  override def toString() = current+" then "+next
}

private[geonet] class AccumulatingResponseRequest[R1<:Request](previous:Response, request:R1) extends AccumulatingRequest[R1] {
  def exec(sideEffect: Option[SideEffect]) = {
    val combined = sideEffect match {
      case Some(e) => e.addEffect(previous.effect)
      case None => previous.effect
    }
    val latestResponse = request.exec(Some(combined))
    AccumulatedResponse(previous,latestResponse)
  }
}
class EffectRequest[R<:Request](effect:SideEffect, request:R) extends Request {
  def exec(sideEffect: Option[SideEffect]) = {
    val combined = sideEffect match {
      case Some(e) => e.addEffect(effect)
      case None => effect
    }
    request.exec(Some(combined))
  }

  override def toString() = request+" modified by "+effect
}

object NoRequest extends Request {
  def exec(sideEffect: Option[SideEffect]) = new EmptyResponse(this,sideEffect getOrElse NoEffect)

  override def toString() = "NoRequest"
}

abstract class MPFormPart {
  protected def write(s:StringBuilder)(implicit out:OutputStream) = {
    Log(Log.RequestMPForm, s)
    out.write(s.toString.getBytes("UTF-8"))
  }
  def write(implicit out:OutputStream):Unit
}
case class FileMPFormPart(name:String,contentType:String,fileName:String, data:Iterator[Byte]) extends MPFormPart {
  def write(implicit out: OutputStream) {
    val prefix = new StringBuilder()
    prefix ++= "Content-Disposition: form-data; name=\"" ++= name ++= "\"; filename=\"" ++= fileName ++=
      "\"\r\n" ++= "Content-Type: " ++= contentType ++= "\r\n\r\n"
    write(prefix)
    Log(Log.RequestMPForm," -- FileData being written --")
    val bufferSize = 1024*8
    val buffer = new Array[Byte](bufferSize)
    data.sliding(bufferSize,bufferSize) foreach { bytes =>
      bytes.copyToArray(buffer)
      out.write(buffer,0,bytes.size)
    }
    write(new StringBuilder("\r\n"))
  }
}
case class FieldMPFormPart(name:String, value:String) extends MPFormPart {
  def write(implicit out: OutputStream) {
    val data = new StringBuilder()
    data ++= "Content-Disposition: form-data; name=\"" ++= name ++=
      "\"\r\n\r\n" ++= "" ++= value++= "\r\n"
    write(data)
  }
}

object MultiPartFormRequest {
  def boundary="----GeonetworkFormBoundaryKjKljasfkl"
}
import MultiPartFormRequest.boundary
abstract class MultiPartFormRequest[RF <: Response](_serv:String, responseFactory:ResponseFactory[RF], form:MPFormPart*)
  extends AbstractRequest(responseFactory, _serv, "multipart/form-data; boundary="+boundary) {

  require(form.size > 0, "At least one form element is required")

  override def writeData(out: OutputStream) = {
    def write(s:String) = {
      Log(Log.RequestMPForm, s)
      out.write(s.getBytes("UTF-8"))
    }
    val startField = "--"+boundary+"\r\n"
    form.foreach {part =>
      write(startField)
      part.write(out)
    }
    write("\r\n--" + boundary + "--\r\n")
  }
}
