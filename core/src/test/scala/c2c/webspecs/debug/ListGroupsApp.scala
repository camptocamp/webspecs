package c2c.webspecs
package debug

import c2c.webspecs.geonetwork._
import c2c.webspecs.login.LoginRequest

object ListGroupsApp extends App {
  ExecutionContext.withDefault{ implicit context =>

    val res = (LoginRequest
        ("admin","admin") then ListGroups)(None)
    assert(res.value.nonEmpty)
    res.value foreach println
  }
}