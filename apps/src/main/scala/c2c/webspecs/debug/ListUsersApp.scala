package c2c.webspecs
package debug

import c2c.webspecs.geonetwork.ListUsers
import c2c.webspecs.login.LoginRequest

object ListUsersApp extends App {
  ExecutionContext.withDefault{ implicit context =>

    val res = (LoginRequest("testjesse","testjesse") then ListUsers)(None)
    assert(res.value.nonEmpty)
    res.value foreach println
  }
}