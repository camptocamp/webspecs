package c2c.webspecs
package geonetwork
package geocat

import c2c.webspecs.{BasicHttpValue, BasicValueFactory, AbstractGetRequest}

/**
 * shared.users.list request that can filter by the name input value
 *
 * for example GeocatListUsers("jess") will list all users containing jess in
 * name, surname, username, address, email...
 *
 * Results are similar to that returned by ListUsers but has Validated as well
 */
object GeocatListUsers
  extends AbstractGetRequest[String,List[User with UserRef with Validateable]](
    "shared.user.list!",
    SelfValueFactory(),
    IdP("name")
  )
  with BasicValueFactory[List[User with UserRef with Validateable]] {
  def createValue(rawValue: BasicHttpValue) = {
    rawValue.toXmlValue.withXml(xml => {
      val users = xml \\ "response" \\ "record" map {record =>
        val basicUser = User fromRecord record
        new User(basicUser) with UserRef with Validateable {
          def userId: String = basicUser.userId
          def validated = xml \\ "validated" nonEmpty
        }
      }

      users.toList
    })
  }
}