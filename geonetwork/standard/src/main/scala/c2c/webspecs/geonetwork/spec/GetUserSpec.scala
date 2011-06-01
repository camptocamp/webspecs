package c2c.webspecs.geonetwork.spec

import org.specs2.specification.{Then, Given, Step}
import org.specs2.execute.{Success, Result}
import org.specs2.matcher.Expectable
import c2c.webspecs.geonetwork._
import javax.management.remote.rmi._RMIConnection_Stub
import sun.misc.Request

class GetUserSpec extends GeonetworkSpecification { def is =

  "This specification tests editing metadata"                     ^ Step(setup) ^
    "Given a request listing all user's ids"                      ^ AllUsers ^
    "At least one user exists"                                  ^ AdminUserExists ^
    "All users should be accessible"                              ^ UserIsAccessible ^
                                                                  Step(tearDown)

  object AllUsers extends Given[List[User with UserRef]] {
    def extract(text: String) = {
      (config.adminLogin then ListUsers)(None).value
    }
  }

  object AdminUserExists extends Then[List[User with UserRef]] {

    def extract(users : List[User with UserRef], text: String) =
      {
        val expectable = users.map{_.userId} aka "users"
        (expectable must not beEmpty) and
        (users.map{_.username} must contain ("admin"))
      }
  }

  object UserIsAccessible extends Then[List[User with UserRef]] {

    def extract(users: List[User with UserRef], text: String) = {
      val request = (users foldLeft config.adminLogin) {
        case (result,next) =>
          result then GetUser(next.userId)
      }
      request(None) must have200ResponseCode

    }
  }
}
