package c2c.webspecs.geonetwork.spec

import org.specs2.specification.{ Then, Given, Step }
import org.specs2.execute.{ Success, Result }
import c2c.webspecs.geonetwork.{ GetUser, ListUsers, GeonetworkSpecification }
import c2c.webspecs.Properties

class GetUserSpec extends GeonetworkSpecification() {
  def is =

    "This specification tests editing metadata"                           ^ Step(setup) ^
      "Given a request listing all user's ids"                            ^ AllUsers.give ^
      "At least the one user exists"                                      ^ NotEmpty.then ^
      "All users should be accessible"                                    ^ UserIsAccessible.then ^
      Step(tearDown)

  type AllUsersOutput = List[User with UserRef]
  val AllUsers = 
    (text: String) => (config.adminLogin then ListUsers)(None).value

  val AdminUserExists =
    (userIds: List[String], text: String) => userIds aka "userIds" must contain (Properties.get(config.ADMIN_USER_KEY))

  val UserIsAccessible = (userIds: List[String], text: String) => {
    val seed: Result = Success(): Result
    (userIds foldLeft seed) {
      case (result, next) =>
        result and makeRequest(next)
    }
  }

  private def makeRequest(id: String) =
    (config.adminLogin then GetUser(id))(None) must have200ResponseCode

}
