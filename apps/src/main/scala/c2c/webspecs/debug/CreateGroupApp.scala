package c2c.webspecs
package debug

import c2c.webspecs.geonetwork._

object CreateGroupApp extends App {
  ExecutionContext.withDefault{ implicit context =>

    val groupName = "new_group"
    val createGroup = CreateGroup(new Group(groupName))

    val res = (Login("admin","admin") then createGroup)(None)
    println("new groupId == "+ res.value.id )
  }
}