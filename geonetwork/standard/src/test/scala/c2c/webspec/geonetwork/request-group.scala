package c2c.webspecs
package geonetwork

import org.apache.http.entity.mime.content._
import scalax.io.Codec


object CreateGroup {
  def apply(groupName:String) = new CreateGroup("name" -> new StringBody(groupName))
  def apply(groupName:String,description:String) = new CreateGroup(
      "name" -> new StringBody(groupName,Codec.UTF8.charSet),
      "description" -> new StringBody(description,Codec.UTF8.charSet)
  )
}
class CreateGroup(form:(String,ContentBody)*) extends MultiPartFormRequest("group.update",XmlValueFactory,form:_*)

case class DeleteGroup(groupId:String,deleteUser:Boolean)
  extends AbstractGetRequest("group.remove", XmlValueFactory, "id" -> groupId, "users" -> (if(deleteUser) "delete" else ""))

