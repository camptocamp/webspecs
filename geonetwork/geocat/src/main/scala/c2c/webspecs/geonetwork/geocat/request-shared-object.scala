package c2c.webspecs
package geonetwork
package geocat

import java.net.URL
import collection.SeqProxy

object SharedObjectTypes extends Enumeration {
  type SharedObjectType = Value
  val contacts, extents, formats, keywords, deleted = Value
}
import SharedObjectTypes._

case class SharedObject(id:Int,
                        url:Option[URL],
                        description:String,
                        objType:SharedObjectType)
class SharedObjectList(val basicValue:BasicHttpValue,
                       val self:List[SharedObject]) extends SeqProxy[SharedObject] with XmlValue

class SharedObjectListFactory(objType:SharedObjectType) extends BasicValueFactory[SharedObjectList] {
  override def createValue(rawValue: BasicHttpValue) = {
    val xmlValue = XmlValueFactory.createValue(rawValue)
    val list = xmlValue.withXml{
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
    new SharedObjectList(rawValue,list)

  }
}

case class ListNonValidated(sharedType:SharedObjectType)
  extends DeprecatedAbstractGetRequest[Any,SharedObjectList]("reusable.non_validated.list", new SharedObjectListFactory(sharedType), "type" -> sharedType.toString)
case object ListNonValidatedContacts
  extends DeprecatedAbstractGetRequest[Any,SharedObjectList]("reusable.non_validated.list", new SharedObjectListFactory(contacts), "type" -> contacts.toString)
case object ListNonValidatedFormats
  extends DeprecatedAbstractGetRequest[Any,SharedObjectList]("reusable.non_validated.list", new SharedObjectListFactory(formats), "type" -> formats.toString)
case object ListNonValidatedExtents
  extends DeprecatedAbstractGetRequest[Any,SharedObjectList]("reusable.non_validated.list", new SharedObjectListFactory(extents), "type" -> extents.toString)
case object ListNonValidatedKeywords
  extends DeprecatedAbstractGetRequest[Any,SharedObjectList]("reusable.non_validated.list", new SharedObjectListFactory(keywords),"type" -> keywords.toString)
case object ListDeletedSharedObjects
  extends DeprecatedAbstractGetRequest[Any,SharedObjectList]("reusable.non_validated.list", new SharedObjectListFactory(deleted), "type" -> deleted.toString)

case class ReferencingMetadata(mdId:Int,title:String,ownerName:String,email:String)

class ReferencingMetadataList(val basicValue:BasicHttpValue,
                              val self:List[ReferencingMetadata]) extends SeqProxy[ReferencingMetadata] with XmlValue

object ReferencingMetadataListFactory extends BasicValueFactory[ReferencingMetadataList] {

  override def createValue(rawValue: BasicHttpValue) = {
    val xmlValue = XmlValueFactory.createValue(rawValue)
    val list = xmlValue.withXml {
      xml =>
        (xml \\ "record").toList map {
          result =>
            val id = (result \\ "id" text).toInt
            val title = (result \\ "title" text)
            val ownerName = (result \\ "name" text)
            val email = (result \\ "email" text)
            ReferencingMetadata(id,title,ownerName, email)
      }
    }

    new ReferencingMetadataList(rawValue,list)
  }
}
case class ListReferencingMetadata(sharedObjectId:Int, sharedType:SharedObjectType)
  extends DeprecatedAbstractGetRequest[Any,ReferencingMetadataList]("reusable.references", ReferencingMetadataListFactory, "id" -> sharedObjectId.toString, "type" -> sharedType.toString)

case class RejectNonValidatedObject(sharedObjectId:String, sharedType:SharedObjectType, rejectionMessage:String="This is a test script rejecting your object, if this is a mistake please inform the system administrators")
  extends DeprecatedAbstractGetRequest[Any,IdValue]("reusable.reject", ExplicitIdValueFactory(sharedObjectId), "id" -> sharedObjectId.toString, "type" -> sharedType.toString, "msg" -> rejectionMessage)
case class DeleteSharedObject(sharedObjectId:String, sharedType:SharedObjectType)
  extends DeprecatedAbstractGetRequest[Any,IdValue]("reusable.delete", ExplicitIdValueFactory(sharedObjectId), "id" -> sharedObjectId.toString, "type" -> sharedType.toString)
case class ValidateSharedObject(sharedObjectId:String, sharedType:SharedObjectType)
  extends DeprecatedAbstractGetRequest[Any,IdValue]("reusable.validate", ExplicitIdValueFactory(sharedObjectId), "id" -> sharedObjectId.toString, "type" -> sharedType.toString)
