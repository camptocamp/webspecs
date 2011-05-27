package c2c.webspecs.geonetwork.spec

import org.specs2.specification.{Then, Given, Step}
import org.specs2.execute.{Success, Result}
import c2c.webspecs.geonetwork.{GetUser, ListUsers, GeonetworkSpecification}

class GetUserSpec extends GeonetworkSpecification { def is =

  "This specification tests editing metadata"                     ^ Step(setup) ^
    "Given a request listing all user's ids"                      ^ AllUsers ^
    "At least one user exists"                                  ^ AdminUserExists ^
    "All users should be accessible"                              ^ UserIsAccessible ^
                                                                  Step(tearDown)

  object AllUsers extends Given[List[String]] {
    def extract(text: String): List[String] = {
      (config.adminLogin then ListUsers)(None).value map {_.userId}
    }
  }

  object AdminUserExists extends Then[List[String]] {

    def extract(userIds: List[String], text: String) =
      userIds aka "userIds" must not beEmpty
  }

  object UserIsAccessible extends Then[List[String]] {
    def makeRequest(id:String) =
      (config.adminLogin then GetUser(id))(None) must have200ResponseCode

    def extract(userIds: List[String], text: String) = {
      val seed: Result = Success(): Result
      (userIds foldLeft seed) {
        case (result,next) =>
          result and makeRequest(next)
      }
    }
  }
}
