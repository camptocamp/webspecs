package c2c.webspecs
package debug

import c2c.webspecs.geonetwork._
import c2c.webspecs.login.LoginRequest

object ListGroupsApp extends WebspecsApp {
    val res = (LoginRequest
        ("admin","admin") then ListGroups).execute()
    assert(res.value.nonEmpty)
    res.value foreach println
}