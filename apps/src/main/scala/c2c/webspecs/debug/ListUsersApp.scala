package c2c.webspecs
package debug

import c2c.webspecs.geonetwork.ListUsers

object ListUsersApp extends App {
  ExecutionContext.withDefault{ implicit context =>

    val res = (Login("testjesse","testjesse") then ListUsers)(None)
    assert(res.value.nonEmpty)
    res.value foreach println
  }
}