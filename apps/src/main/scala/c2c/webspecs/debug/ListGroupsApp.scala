package c2c.webspecs
package debug

import c2c.webspecs.geonetwork._

object ListGroupsApp extends App {
  ExecutionContext.withDefault{ implicit context =>

    val res = (Login("admin","admin") then ListGroups)(None)
    assert(res.value.nonEmpty)
    res.value foreach println
  }
}