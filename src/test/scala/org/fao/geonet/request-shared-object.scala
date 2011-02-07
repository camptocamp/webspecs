package org.fao.geonet

import java.net.URL

object SharedObjectTypes extends Enumeration {
  type SharedObjectType = Value
  val contacts, extents, formats, keywords, deleted = Value
}
import SharedObjectTypes._

case class SharedObject(id:Int, url:Option[URL], description:String, objType:SharedObjectType)
class ListSharedObjectResponse(response:Response, objType:SharedObjectType) extends DecoratingResponse[Response](response.request, response) with XmlResponse {
  lazy val list = withXml{
    xml =>
      (xml \\ "record").toList map {
        record =>
          val id = (record \\ "id" text).toInt
          val url = {
            val urlTag = record \\ "url"
            if(urlTag.isEmpty) None
            else Some(new URL(record \\ "url" text))
          }
          val desc = (record \\ "desc" text)
          SharedObject(id,url,desc,objType)
      }
  }
}
class ListSharedObjectResponseFactory[T <: SharedObjectType](objType:SharedObjectType) extends ResponseFactory[ListSharedObjectResponse] {
  def wrapResponse(basicResponse: Response) = new ListSharedObjectResponse(basicResponse,objType)
}
case class ListNonValidated(sharedType:SharedObjectType)
  extends GetRequest("reusable.non_validated.list", new ListSharedObjectResponseFactory(sharedType), "type" -> sharedType.toString)
case object ListNonValidatedContacts
  extends GetRequest("reusable.non_validated.list", new ListSharedObjectResponseFactory(contacts), "type" -> contacts.toString)
case object ListNonValidatedFormats
  extends GetRequest("reusable.non_validated.list", new ListSharedObjectResponseFactory(formats), "type" -> formats.toString)
case object ListNonValidatedExtents
  extends GetRequest("reusable.non_validated.list", new ListSharedObjectResponseFactory(extents), "type" -> extents.toString)
case object ListNonValidatedKeywords
  extends GetRequest("reusable.non_validated.list", new ListSharedObjectResponseFactory(keywords),"type" -> keywords.toString)
case object ListDeletedSharedObjects
  extends GetRequest("reusable.non_validated.list", new ListSharedObjectResponseFactory(deleted), "type" -> deleted.toString)

case class ReferencingMetadata(mdId:Int,title:String,ownerName:String,email:String)
case class ReferencingMetadataResponse(res:Response)
  extends DecoratingResponse(res) with XmlResponse{
  lazy val list = withXml {
    xml =>
      xml \\ "record" map {
        result =>
          val id = (result \\ "id" text).toInt
          val title = (result \\ "title" text)
          val ownerName = (result \\ "name" text)
          val email = (result \\ "email" text)
          ReferencingMetadata(id,title,ownerName, email)
    }
  }
}
object ReferencingMetadataResponseFactory extends ResponseFactory[ReferencingMetadataResponse]  {
  def wrapResponse(basicResponse: Response) = new ReferencingMetadataResponse(basicResponse)
}
case class ListReferencingMetadata(sharedObjectId:Int, sharedType:SharedObjectType)
  extends GetRequest("reusable.references", ReferencingMetadataResponseFactory, "id" -> sharedObjectId.toString, "type" -> sharedType.toString)

case class RejectNonValidatedObject(sharedObjectId:Int, sharedType:SharedObjectType, rejectionMessage:String="This is a test script rejecting your object, if this is a mistake please inform the system administrators")
  extends GetRequest("reusable.reject", ParamIdResponseFactory, "id" -> sharedObjectId.toString, "type" -> sharedType.toString, "msg" -> rejectionMessage)
case class DeleteSharedObject(sharedObjectId:Int, sharedType:SharedObjectType)
  extends GetRequest("reusable.delete", ParamIdResponseFactory, "id" -> sharedObjectId.toString, "type" -> sharedType.toString)
case class ValidateSharedObject(sharedObjectId:Int, sharedType:SharedObjectType)
  extends GetRequest("reusable.validate", ParamIdResponseFactory, "id" -> sharedObjectId.toString, "type" -> sharedType.toString)
