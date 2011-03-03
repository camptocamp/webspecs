package c2c.webspecs
package geonetwork

import xml.NodeSeq
import java.io.{FileWriter, BufferedWriter}
import geonetwork.AddSites.{ContactAddSite, AddSite}

trait EditValue extends IdValue {
  def version:String
}

object EditValueFactory extends BasicValueFactory[EditValue] {


  override def createValue(rawValue: BasicHttpValue) = apply(rawValue)

  def apply(rawValue: BasicHttpValue) = new EditValue {
    import XmlUtils._
    protected def basicValue = rawValue
    lazy val id = withXml( xml => lookupId(xml) )
    lazy val version = withXml( xml => lookupVersion(xml) )
  }

}

case class CreateMetadata(constants:GeonetConfig, templateId:String)
  extends DeprecatedAbstractGetRequest[Any,EditValue]("metadata.create",EditValueFactory,"group" -> constants.groupId, "id" -> templateId)

import xml.{Node, NodeSeq}

class AddValue(val basicValue:BasicHttpValue,
               val newElement:Node,
               val newXml:NodeSeq ) extends EditValue {
  lazy val editVal = EditValueFactory(basicValue)
  lazy val id = editVal.id
  lazy val version = editVal.version
  lazy val href = XLink hrefFrom newElement
}
abstract class Add(_serv:String, id:String, editVersion:String, nodeRef:String, addSite:AddSite, extraFields:(String,Any)*)
  extends Request[EditValue,AddValue] {
  val addRequest = FormPostRequest(_serv,
    extraFields.map{case (key,value) => (key,value.toString)} ++ List("id" -> id,
    "version" -> editVersion,
    "ref" -> nodeRef,
    "name" -> addSite.toString,
    "role" -> "embed",
    "schema" -> "iso19139.che",
    "currTab" -> "complete"):_*)

  val beforeMetadata = GetMetadataXml(id)
  val afterMetadata = GetMetadataXml(id)


  def apply(in: EditValue)(implicit context: ExecutionContext) = {
    val before = beforeMetadata(None)
    val add = addRequest(None)
    val after = afterMetadata(None)

    val (newElement, newXml) = before.value.withXml { beforeXml =>
      after.value.withXml { afterXml =>
        val beforeElems = (beforeXml \\ addSite.name)
        val afterElems = (afterXml \\ addSite.name).toList
        val newElem = afterElems filterNot {beforeElems contains _}

        if(newElem.size != 1){
          /*val writer = new BufferedWriter(new FileWriter("c:/tmp/"+id+addSite.name+".log"))
          writer.write(">>>>>>>>>>>>>>>>>>>>>>>>>Before:>>>>>>>>>>>>>>>>>>>>>>>>>\n")
          writer.write(beforeXml.toString)
          writer.write("-------------------------After:---------------------------\n")
          writer.write(afterXml.toString)
          writer.write("<<<<<<<<<<<<<<<<<<<<<<<<Finished:<<<<<<<<<<<<<<<<<<<<<<<<<\n")
          writer.close   */
        }

        (newElem, afterXml)
      }
    }

    assert(newElement.size == 1, "Expected there to be 1 new element but instead there was "+newElement.size)

    val value = new AddValue(add.basicValue, newElement.head, newXml)
    new BasicHttpResponse(add.basicValue,value)
  }
}

object AddNewContact {
  def apply(addSite:ContactAddSite=AddSites.contact) = { response:Response[XmlValue] =>
    response.value.withXml { md =>
      implicit val allInput = md \\ "input"
      val nodeRef = XLink.lookupXlinkNodeRef(addSite.toString)(md)
      import XmlUtils._
      new AddNewContact(lookupId, lookupVersion, nodeRef, addSite)
    }
  }
}
class AddNewContact(id:String, editVersion:String, nodeRef:String, addSite:ContactAddSite)
  extends Add("metadata.elem.add",id,editVersion, nodeRef, addSite, "child" -> "")

import MetadataViews.MetadataView
object StartEditing {
  def apply(view:MetadataView = MetadataViews.simple):Response[IdValue] => StartEditing = response => StartEditing(response.value.id,view)
}
case class StartEditing(mdId:String,view:MetadataView)
  extends DeprecatedAbstractGetRequest[Any,EditValue]("metadata.edit",EditValueFactory, "id" -> mdId,"currTab" -> view.toString)

