package c2c.webspecs
package geonetwork

case class DeleteGroup(groupId:String,deleteUsers:Boolean)
  extends AbstractGetRequest(
    "group.remove",
    XmlValueFactory,
    P("id", groupId),
    P("users", if(deleteUsers) "delete" else "")
  )
