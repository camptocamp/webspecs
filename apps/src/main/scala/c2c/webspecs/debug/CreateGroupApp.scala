package c2c.webspecs
package debug

import c2c.webspecs.geonetwork._
import c2c.webspecs.login.LoginRequest

object CreateGroupApp extends App {
  ExecutionContext.withDefault{ implicit context =>

    val groupName = "new_group"
    val createGroup = CreateGroup(new Group(groupName))

    val res = (LoginRequest("admin","admin") then createGroup)(None)
    println("new groupId == "+ res.value.id )
  }
}