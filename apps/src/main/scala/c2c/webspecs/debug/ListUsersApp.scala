package c2c.webspecs
package debug

import c2c.webspecs.geonetwork.ListUsers
import c2c.webspecs.login.LoginRequest
import c2c.webspecs.geonetwork.GeonetworkSpecification

object ListUsersApp extends WebspecsApp {
    val res = (LoginRequest("testjesse","testjesse") then ListUsers).execute()
    assert(res.value.nonEmpty)
    res.value foreach println
}