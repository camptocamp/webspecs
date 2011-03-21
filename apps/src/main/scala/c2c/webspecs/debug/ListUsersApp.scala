package c2c.webspecs
package debug

import c2c.webspecs.geonetwork.ListUsers

object ListUsersApp extends Application {
  ExecutionContext.withDefault{ implicit context =>

    val res = (Login("jeichar","jeichar") then ListUsers)(None)
    assert(res.value.nonEmpty)
    res.value foreach println
  }
}