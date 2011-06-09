package c2c.webspecs.geonetwork
package spec

import org.specs2.specification.{ Then, Given, Step }
import org.specs2.execute.{ Success, Result }
import c2c.webspecs.geonetwork.{ GetUser, ListUsers, GeonetworkSpecification }
import c2c.webspecs.Properties

class GetUserSpec extends GeonetworkSpecification() {
  def is =

    "This specification tests editing metadata"                           ^ Step(setup) ^
      "Given a request listing all user's ids"                            ^ AllUsers.give ^
      "At least the one user exists"                                      ^ NotEmpty.then ^
      "The Admin user must be a user"                                     ^ AdminUserExists.then ^
      "All users should be accessible"                                    ^ UserIsAccessible.then ^
      Step(tearDown)

  type AllUsersResponse = List[User with UserRef]
  val AllUsers = 
    (text: String) => (config.adminLogin then ListUsers)(None).value

  val NotEmpty =
    (users: AllUsersResponse, text: String) => users aka "users" must not beEmpty

  val AdminUserExists =
    (users: AllUsersResponse, text: String) => users.map{_.username} aka "users" must contain (Properties.get(config.ADMIN_USER_KEY))

  val UserIsAccessible = (userIds: AllUsersResponse, text: String) => {
    val seed: Result = Success(): Result
    (userIds foldLeft seed) {
      case (result, next) =>
        result and makeRequest(next.userId)
    }
  }

  private def makeRequest(id: String) =
    (config.adminLogin then GetUser(id))(None) must have200ResponseCode

}
