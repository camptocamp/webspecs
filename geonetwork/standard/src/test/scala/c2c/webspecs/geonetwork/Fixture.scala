package c2c.webspecs.geonetwork

import c2c.webspecs.ExecutionContext
import java.util.UUID

trait Fixture {
  def create(config: GeonetConfig, context: ExecutionContext): Unit

  def delete(config: GeonetConfig, context: ExecutionContext): Unit
}

object GeonetworkFixture {
  def user(requiredProfile:UserProfiles.UserProfile = UserProfiles.Editor) = new Fixture {
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
      val user = User(
        username = username,
        email = email,
        name = name,
        surname=lastname,
        password = username,
        profile = profile)
      val formats = (config.adminLogin then CreateUser(user))(None)(context)
      _id = formats.value.userId
    }
  }


}