package org.fao.geonet

import xml.NodeSeq
import java.io.{FileWriter, BufferedWriter}

object Editing {
  private class EditWrapper(request:Request) extends Request with EditRequest {
    def exec(sideEffect: Option[SideEffect]) = request.exec(sideEffect)
    override def apply[R](f: (Response) => R) = request.apply(f)
    override def apply() = request.apply()
    override def assertPassed() = request.assertPassed
  }
  implicit def chainedRequestToEditRequest[R<:Request with EditRequest](request:ChainedRequest[R]):EditRequest = new EditWrapper(request)
  implicit def effectRequestToEditRequest[R<:Request with EditRequest](request:EffectRequest[R]):EditRequest = new EditWrapper(request)
  implicit def accumulatingRequestToEditRequest[R<:Request with EditRequest](request:AccumulatingRequest[R]):EditRequest = new EditWrapper(request)
}

trait EditFactory {
  protected def inputValue(name:String)(implicit allInput:NodeSeq) = {
    val idInputEl = allInput filter {e => (e \ "@name").text == name}
    val values = idInputEl map {_.attribute("value").get.text}
    values.headOption getOrElse {
      throw new IllegalStateException("Expected to find an id input element. Does the response come from a request that returns a metadata.edit form? ")
    }
  }

  protected def lookupId(implicit allInput:NodeSeq) = inputValue("id")(allInput)
  protected def lookupVersion(implicit allInput:NodeSeq) = inputValue("version")(allInput)

  private def xlinkIdExtractor(nodeName:String) = {
    import java.util.regex.Pattern.quote
    (quote("javascript:displayXLinkSearchBox('/geonetwork/srv/")+".?.?.?"+quote("/metadata.xlink.add',") +
      "(.+?)"+quote(",'"+nodeName+"','")+".+?"+quote("');")).r
  }
  protected def lookupXlinkNodeRef(nodeName:String)(md:NodeSeq) = {
    val IdExtractor = xlinkIdExtractor(nodeName)

    val xlinkSearchBoxLinks = md \\ "a" \\ "@href" map {_.text} collect {
      case IdExtractor(id) => id
    }
    xlinkSearchBoxLinks.headOption getOrElse {throw new IllegalArgumentException(nodeName+" does not have an xlink site in this metadata")}
  }

  protected def withXml[R<:Request](function:NodeSeq => R):Response => R = {
    response =>
     response.xml.fold(
       error => throw error,
       xml => function(xml)
     )
  }

}
object StandardSharedExtents {
  abstract class StandardSharedExtent(val typeName:String,val id:String)
  case object KantonBern extends StandardSharedExtent("gn:kantoneBB", "2")
}
trait EditRequest {
   self : Request =>
  final def editing[R<:Request with EditRequest](next:R)= new ChainedRequest(this,_ => next) with EditRequest
  final def editing[R<:Request with EditRequest](next:Response => R)= new ChainedRequest(this,next) with EditRequest
}

case class CreateMetadata(constants:Constants, templateId:String)
  extends GetRequest("metadata.create",XmlResponseFactory,"group" -> constants.groupId, "id" -> templateId)
  with EditRequest
object AddSites extends Enumeration {
  type AddSite = Value with PrefixedValue

  trait ExtentAddSite extends Value with PrefixedValue
  trait ContactAddSite extends Value with PrefixedValue
  trait FormatAddSite extends Value with PrefixedValue
  trait KeywordAddSite extends Value with PrefixedValue

  private def Contact(name:String) = new PrefixedValueImpl("gmd",name) with ContactAddSite
  val contact = Contact("contact")
  val userContactInfo = Contact("userContactInfo")
  val distributorContact = Contact("distributorContact")
  val citedResponsibleParty = Contact("citedResponsibleParty")
  val processor = Contact("processor")
  val pointOfContact = Contact("pointOfContact")

  private def Format(name:String) = new PrefixedValueImpl("gmd",name) with FormatAddSite
  val distributionFormat = Format("distributionFormat")
  val resourceFormat = Format("distributionFormat")

  val descriptiveKeywords = new PrefixedValueImpl("gmd", "descriptiveKeywords") with KeywordAddSite

  private def Extent(prefix:String,name:String) = new PrefixedValueImpl(prefix,name) with ExtentAddSite
  val extent = Extent("gmd","extent")
  val sourceExtent  = Extent("gmd","sourceExtent")
  val revisionExtent = Extent("che","revisionExtent")

  trait PrefixedValue {
    def name:String
    def prefix:String
  }
  class PrefixedValueImpl(val prefix:String, val name:String) extends Val(name) {
    override def toString() = prefix+":"+super.toString
  }
}
import AddSites.{AddSite,ContactAddSite,ExtentAddSite,FormatAddSite,KeywordAddSite}
import xml.{Node, NodeSeq}

abstract class Add(_serv:String, id:String, editVersion:String, nodeRef:String, addSite:AddSite, extraFields:(String,Any)*)
  extends Request with EditRequest {
  val addRequest = Form(_serv,
    extraFields.map{case (key,value) => (key,value.toString)} ++ List("id" -> id,
    "version" -> editVersion,
    "ref" -> nodeRef,
    "name" -> addSite.toString,
    "role" -> "embed",
    "schema" -> "iso19139.che",
    "currTab" -> "complete"):_*)

  val beforeMetadata = GetMetadataXml(id.toInt, OutputSchemas.IsoRecord)
  val afterMetadata = GetMetadataXml(id.toInt, OutputSchemas.IsoRecord)

  def exec(sideEffect: Option[SideEffect]) = {
    val before = beforeMetadata.exec(sideEffect)
    val add = addRequest.exec(sideEffect)
    val after = afterMetadata.exec(sideEffect)

    val (newElement, newXml) = before.xml.fold(
      error => throw new IllegalStateException("There was no metadata found before the add "+addSite+" was executed", error),
      beforeXml =>
        after.xml.fold(
          error => throw new IllegalStateException("The metadata was not found after the add "+addSite+" was executed", error),
          afterXml => {
            val beforeElems = (beforeXml \\ addSite.name)
            val afterElems = (afterXml \\ addSite.name).toList
            val newElem = afterElems filterNot {beforeElems contains _}

            if(newElem.size != 1){
              val writer = new BufferedWriter(new FileWriter("c:/tmp/"+id+addSite.name+".log"))
              writer.write(">>>>>>>>>>>>>>>>>>>>>>>>>Before:>>>>>>>>>>>>>>>>>>>>>>>>>\n")
              writer.write(beforeXml.toString)
              writer.write("-------------------------After:---------------------------\n")
              writer.write(afterXml.toString)
              writer.write("<<<<<<<<<<<<<<<<<<<<<<<<Finished:<<<<<<<<<<<<<<<<<<<<<<<<<\n")
              writer.close
            }

            (newElem, afterXml)
          }
        )
    )

    assert(newElement.size == 1, "Expected there to be 1 new element but instead there was "+newElement.size)

    AddResponse(this, add, newElement.head, newXml)
  }
}

case class AddResponse(_request:Request, add:Response, newElement:Node, newMetadata:NodeSeq) extends DecoratingResponse(_request, add) {
  val href = XLink hrefFrom newElement

}

object AddExtentXLink extends EditFactory {
  def apply(extent:StandardSharedExtents.StandardSharedExtent,
            withPolygon:Boolean,
            addSite:ExtentAddSite=AddSites.extent) = {
    withXml {
      md =>
        implicit val allInput = md \\ "input"
        val xlink = Config.serviceUrl("xml.extent.get",
          "wfs" -> "default",
          "format" -> (if(withPolygon) "gmd_complete" else "gmd_bbox"),
          "typename" -> extent.typeName,
          "id" -> extent.id,
          "extentTypeCode" -> "true")

        val nodeRef = lookupXlinkNodeRef(addSite.toString)(md)
        new AddXLink(lookupId, lookupVersion, xlink, nodeRef, addSite)
    }
  }
}
class AddXLink(id:String, editVersion:String, xlink:String, nodeRef:String, addSite:AddSite)
  extends Add("metadata.xlink.add", id, editVersion, nodeRef, addSite, "href" -> xlink)
  with EditRequest

object AddNewContact extends EditFactory {
  def apply(addSite:ContactAddSite=AddSites.contact) = {
    withXml {
      md =>
        implicit val allInput = md \\ "input"
        val nodeRef = lookupXlinkNodeRef(addSite.toString)(md)
        new AddNewContact(lookupId, lookupVersion, nodeRef, addSite)
    }
  }
}
class AddNewContact(id:String, editVersion:String, nodeRef:String, addSite:ContactAddSite)
  extends Add("metadata.elem.add",id,editVersion, nodeRef, addSite, "child" -> "")

/*
object ContactRoles extends Enumeration{
  type ContactRole = Value
  val author, originator = Value
}
import ContactRoles.ContactRole
object EditContact extends EditFactory {
  def apply(orgName:String="", posName:String="", email:String="", role:ContactRole=ContactRoles.originator, firstName:String="", lastName:String="") = {
    withXml {
      md =>
        implicit val allInput = md \\ "input"
        val nodeRef = lookupXlinkNodeRef(addSite.toString)(md)
        new Edit(lookupId,lookupVersion)
    }
  }
}

class Edit(id:Int, version:Int, fields:(String,String)*)
  extends FormRequest("metadata.update", List("id" -> id, "version" -> version) ++ fields :_*)
  with EditRequest
*/

import MetadataViews.MetadataView
object StartEditing {
  def apply(view:MetadataView = MetadataViews.simple):Response => StartEditing = MdRequestUtil.loadId(id => StartEditing(id,view))
}
case class StartEditing(mdId:Int,view:MetadataView)
  extends GetRequest("metadata.edit",XmlResponseFactory, "id" -> mdId,"currTab" -> view.toString)
  with EditRequest

