package org.fao.geonet


object CreateGroup {
  def apply(groupName:String) = new CreateGroup(FieldMPFormPart("name", groupName))
  def apply(groupName:String,description:String) = new CreateGroup(
      FieldMPFormPart("name", groupName),
      FieldMPFormPart("description", description)
  )
}
class CreateGroup(form:MPFormPart*) extends MultiPartFormRequest("group.update",XmlResponseFactory,form:_*)

case class DeleteGroup(groupId:String,deleteUser:Boolean)
  extends GetRequest("group.remove", XmlResponseFactory, "id" -> groupId, "users" -> (if(deleteUser) "delete" else ""))

