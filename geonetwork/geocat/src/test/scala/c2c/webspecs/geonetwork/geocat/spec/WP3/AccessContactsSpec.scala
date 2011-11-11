package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step
import c2c.webspecs.{XmlValue, Response}
import geonetwork.UserRef
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AccessContactsSpec extends GeocatSpecification { def is =

  "This specification tests accessing shared users"      ^ Step(setup) ^
    "Listing all users ${*}"                             ^ searchContacts.toGiven ^
      "Should be a successful http request (200 response code)"               ^ l200Response ^
      "Should show user name"                            ^ listUserNames.toThen    ^
      "Should list emails"                               ^ listEmails.toThen   ^
      "Should list names"                                ^ listNames.toThen   ^
      "Should list profile"                              ^ listProfiles.toThen   ^
      "Should list validation"                           ^ listValidation.toThen   ^
                                                           end ^
    "Gettings a user in iso xml"                         ^ contactInIso.toGiven  ^
      "Should be a successful http request (200 response code)"               ^ i200Response      ^
      "Should show name"                                 ^ isoName.toThen      ^
      "Should have surname"                              ^ isoLastName.toThen   ^
      "Should have position in all localisedStrings"     ^ localisedPosition.toThen   ^
                                                           end ^
    "Searching for ${"+userFixture.name+"}"              ^ searchContacts.toGiven  ^
      "Should be a successful http request (200 response code)"               ^ l200Response      ^
      "Should list all users with that name"             ^ hasNameInData.toThen   ^
                                                           end ^
                                                           Step(tearDown)    ^
    "UserFixture should be deleted"                      ! fixtureIsGone


  type ListUserResponse = Response[List[User with UserRef with Validateable]]

  def find[U](list:ListUserResponse)(map: User with UserRef with Validateable => U) = { list.value.find(_.userId == userFixture.id).map(map)}
  val searchContacts = (s:String) => 
    GeocatListUsers.execute(extract1(s).replace('*',' ').trim()):ListUserResponse
  val l200Response = a200ResponseThen.narrow[ListUserResponse]

  val listUserNames = (response:ListUserResponse) => 
    response.value.map{_.username} must contain (userFixture.username)

  val listNames = (response:ListUserResponse) => find(response)(_.name) must beSome(userFixture.name)

  val listProfiles = (response:ListUserResponse) => find(response)(_.profile) must beSome(SharedUserProfile)

  val listEmails = (response:ListUserResponse) => find(response)(_.email) must beSome(userFixture.email)

  val listValidation= (response:ListUserResponse) => find(response)(_.validated) must beSome(true)

  val hasNameInData = (response:ListUserResponse) => {
    import userFixture.{name => fixName}
    val usersMissingExpectedData = response.value.filterNot(c => (c.name contains fixName) || (c.surname contains fixName) || (c.email contains fixName) || (c.username contains fixName))
    usersMissingExpectedData must beEmpty
  }

  val contactInIso = (s:String) =>
    (GetRequest("xml.user.get","id" -> userFixture.id).execute()):Response[XmlValue]
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
  val localisedPosition = (r:Response[XmlValue]) => {
      val positionElems = r.value.getXml \\ "positionName"
      val localisedStrings = positionElems \\ "LocalisedCharacterString"
      val characterStrings = positionElems \\ "CharacterString"
      val de = localisedStrings find (_ @@ "locale" == List("#DE"))
      val en = localisedStrings find (_ @@ "locale" == List("#EN"))
      val fr = localisedStrings find (_ @@ "locale" == List("#FR"))
      (positionElems must haveSize (1)) and
        (localisedStrings must haveSize (3)) and
        (characterStrings must beEmpty) and
        (de.get.text.trim must_== userFixture.position.translations("de")) and
        (en.get.text.trim must_== userFixture.position.translations("en")) and
        (fr.get.text.trim must_== userFixture.position.translations("fr"))
  }
  def fixtureIsGone =
    ExecutionContext.withDefault{c =>
      val response = (config.login then GetRequest("xml.user.get", "id" -> userFixture.id)).execute()(c,uriResolver).value
      response.withHtml(_ \\ "record" must beEmpty)
    }

  lazy val userFixture = GeocatFixture.sharedUser(true)
  override lazy val fixtures = Seq(userFixture)
}