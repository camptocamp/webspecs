package c2c.webspecs
package geonetwork

import c2c.webspecs.ExecutionContext
import java.util.UUID

object GeonetworkFixture {
  def user(requiredProfile:UserProfiles.UserProfile = UserProfiles.Editor) = new Fixture[GeonetConfig] {
    val profile = requiredProfile
    val name = "Web"
    val lastname = "Specs"
    val username = "WebSpecs_"+UUID.randomUUID().toString
    val email = "Webspecs@camptocamp.com"
    private var _id:String = _

    def id = _id

    def delete(config: GeonetConfig, context: ExecutionContext) =
      (config.adminLogin then DeleteUser(id))(None)(context)

    def create(config: GeonetConfig, context: ExecutionContext) = {
      val userReq = User(
        username = username,
        email = email,
        name = name,
        surname=lastname,
        password = username,
        profile = profile)
      val user = (config.adminLogin then CreateUser(userReq))(None)(context)
      _id = user.value.userId
    }
  }
}