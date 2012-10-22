package c2c.webspecs
package debug

import c2c.webspecs.geonetwork._
import c2c.webspecs.login.LoginRequest

object CreateGroupApp extends WebspecsApp {
  def referenceSpecClass = classOf[GeonetworkSpecification]
    val groupName = "new_group"
    val createGroup = CreateGroup(new Group(groupName))

    val res = (LoginRequest("admin","admin") then createGroup).execute()
    println("new groupId == "+ res.value.id )
}