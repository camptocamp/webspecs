package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step
import c2c.webspecs.{XmlValue, Response}
import geonetwork.UserRef

class AccessContacts extends GeonetworkSpecification { def is =

  "This specification tests accessing shared users"     ^ Step(setup) ^
    "Listing all users"                                 ^ listContacts.give ^
      "Should suceed with a 200 response"                ^ l200Response ^
      "Should show user name"                            ^ listUserNames.then    ^
      "Should list emails"                               ^ listEmails.then   ^
      "Should list names"                                ^ listNames.then   ^
      "Should list profile"                              ^ listProfiles.then   ^
      "Should list validation"                           ^ listValidation.then   ^
                                                         end ^
    "Gettings a user in iso xml"                         ^ contactInIso.give  ^
      "Should suceed with a 200 response"                ^ i200Response      ^
      "Should show name"                                 ^ isoName.then      ^
      "Should have surname"                                ^ isoLastName.then   ^
                                                           Step(tearDown)    ^
    "UserFixture should be deleted"                      ! fixtureIsGone


  type ListUserResponse = Response[List[User with UserRef with Validateable]]

  def find[U](list:ListUserResponse)(map: User with UserRef with Validateable => U) = { list.value.find(_.userId == userFixture.id).map(map)}
  val listContacts = (_:String) => GeocatListUsers(""):ListUserResponse
  val l200Response = a200ResponseThen.narrow[ListUserResponse]
  val listUserNames = (response:ListUserResponse, _:String) => {
    response.value.map{_.username} must contain (userFixture.username)
  }
  val listNames = (response:ListUserResponse, _:String) => {
    find(response)(_.name) must beSome(userFixture.name)
  }
  val listProfiles = (response:ListUserResponse, _:String) => {
    find(response)(_.profile) must beSome(userFixture.profile)
  }
  val listEmails = (response:ListUserResponse, _:String) => {
    find(response)(_.email) must beSome(userFixture.email)
  }
  val listValidation= (response:ListUserResponse, _:String) => {
    find(response)(_.validated) must beSome(true)
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