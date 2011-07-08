package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step
import c2c.webspecs.{XmlValue, Response}
import geonetwork.UserRef

class AccessContactsSpec extends GeonetworkSpecification { def is =

  "This specification tests accessing shared users"      ^ Step(setup) ^
    "Listing all users ${ }"                             ^ searchContacts.toGiven ^
      "Should succeed with a 200 response"               ^ l200Response ^
      "Should show user name"                            ^ listUserNames.toThen    ^
      "Should list emails"                               ^ listEmails.toThen   ^
      "Should list names"                                ^ listNames.toThen   ^
      "Should list profile"                              ^ listProfiles.toThen   ^
      "Should list validation"                           ^ listValidation.toThen   ^
                                                           end ^
    "Gettings a user in iso xml"                         ^ contactInIso.toGiven  ^
      "Should succeed with a 200 response"               ^ i200Response      ^
      "Should show name"                                 ^ isoName.toThen      ^
      "Should have surname"                              ^ isoLastName.toThen   ^
                                                           end ^
    "Searching for ${"+userFixture.name+"}"              ^ searchContacts.toGiven  ^
      "Should succeed with a 200 response"               ^ l200Response      ^
      "Should list all users with that name"             ^ hasNameInData.toThen   ^
                                                           end ^
                                                           Step(tearDown)    ^
    "UserFixture should be deleted"                      ! fixtureIsGone


  type ListUserResponse = Response[List[User with UserRef with Validateable]]

  def find[U](list:ListUserResponse)(map: User with UserRef with Validateable => U) = { list.value.find(_.userId == userFixture.id).map(map)}
  val searchContacts = (s:String) => GeocatListUsers(extract1(s)):ListUserResponse
  val l200Response = a200ResponseThen.narrow[ListUserResponse]

  val listUserNames = (response:ListUserResponse) => response.value.map{_.username} must contain (userFixture.username)

  val listNames = (response:ListUserResponse) => find(response)(_.name) must beSome(userFixture.name)

  val listProfiles = (response:ListUserResponse) => find(response)(_.profile) must beSome(userFixture.profile)

  val listEmails = (response:ListUserResponse) => find(response)(_.email) must beSome(userFixture.email)

  val listValidation= (response:ListUserResponse) => find(response)(_.validated) must beSome(true)

  val hasNameInData = (response:ListUserResponse) => {
    import userFixture.{name => fixName}
    val usersMissingExpectedData = response.value.filterNot(c => (c.name contains fixName) || (c.surname contains fixName) || (c.email contains fixName) || (c.username contains fixName))
    usersMissingExpectedData must beEmpty
  }

  val contactInIso = (s:String) =>
    (GetRequest("xml.user.get","id" -> userFixture.id)(None)):Response[XmlValue]
  val i200Response = a200ResponseThen.narrow[Response[XmlValue]]
  val isoName = (r:Response[XmlValue], _:String) =>
    r.value.withXml { xml =>
      val nameElems = xml \\ "individualFirstName"
      ( (nameElems must haveSize (1)) and
        (nameElems.head.text.trim must_== userFixture.name)
      )
    }
  val isoLastName = (r:Response[XmlValue], _:String) =>
    r.value.withXml { xml =>
      val nameElems = xml \\ "individualLastName"
      ( (nameElems must haveSize (1)) and
        (nameElems.head.text.trim must_== userFixture.lastname)
      )
    }

  def fixtureIsGone =
    ExecutionContext.withDefault{c =>
      val response = (config.login then GetRequest("xml.user.get", "id" -> userFixture.id))(None)(c).value
      response.withXml(_ \\ "record" must beEmpty)
    }

  lazy val userFixture = GeonetworkFixture.user(SharedUserProfile)
  override lazy val fixtures = Seq(userFixture)
}