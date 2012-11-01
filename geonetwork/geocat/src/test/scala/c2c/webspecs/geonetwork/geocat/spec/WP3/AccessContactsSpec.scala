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
    "Listing all users"                             	 ^ Step(allSharedUsers) ^
      "Should be a successful http request (200 response code)"               ! {allSharedUsers must haveA200ResponseCode} ^
      "Should show user name"                            ! checkUserNames    ^
      "Should list emails"                               ! checkEmails   ^
      "Should list names"                                ! checkNames   ^
      "Should list profile"                              ! checkProfiles   ^
      "Should list validation"                           ! checkValidation   ^
                                                           endp ^
    "Gettings a user in iso xml"                         ^ contactInIso.toGiven  ^
      "Should be a successful http request (200 response code)"               ^ i200Response      ^
      "Should show name"                                 ^ isoName.toThen      ^
      "Should have surname"                              ^ isoLastName.toThen   ^
      "contactInstruction has xsi:type attribute"        ^ contactInstructionHasXsiType.toThen   ^
      "Should have position in all localisedStrings"     ^ localisedPosition.toThen   ^
                                                           end ^
    "Searching for "+userFixture.name+""                 ^ Step(sharedUsersSearchedByName)  ^
      "Should be a successful http request (200 response code)"               ! {allSharedUsers must haveA200ResponseCode}      ^
      "Should list all users with that name"             ! hasNameInData   ^
                                                           end ^
                                                           Step(tearDown)    ^
    "UserFixture should be deleted"                      ! fixtureIsGone


  type ListUserResponse = Response[List[User with UserRef with Validateable]]

  def find[U](list:ListUserResponse)(map: User with UserRef with Validateable => U) = { list.value.find(_.userId == userFixture.id).map(map)}
  def searchContacts(s:String) = 
    GeocatListUsers.execute(s.trim()):ListUserResponse

  lazy val allSharedUsers = searchContacts(" ")
  lazy val sharedUsersSearchedByName = searchContacts(userFixture.name)
  val l200Response = a200ResponseThen.narrow[ListUserResponse]

  def checkUserNames =
    allSharedUsers.value.map{_.username} must contain (userFixture.username)

  def checkNames = find(allSharedUsers)(_.name) must beSome(userFixture.name)

  def checkProfiles = find(allSharedUsers)(_.profile) must beSome(SharedUserProfile)

  def checkEmails = find(allSharedUsers)(_.email) must beSome(userFixture.email)

  def checkValidation= find(allSharedUsers)(_.validated) must beSome(true)

  def hasNameInData = {
    import userFixture.{name => fixName}
    val usersMissingExpectedData = sharedUsersSearchedByName.value.filterNot(c => (c.name contains fixName) || (c.surname contains fixName) || (c.email contains fixName) || (c.username contains fixName))
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
  val contactInstructionHasXsiType = (r:Response[XmlValue], _:String) =>
    r.value.withXml { xml =>
      val ci = xml \\ "contactInstructions"
      val typeAtt = ci \@ "xsi:type"
      typeAtt must_== List("gmd:PT_FreeText_PropertyType")
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
      response.withHtml(_ \\ "response" \\ "record" must beEmpty)
    }

  lazy val userFixture = GeocatFixture.sharedUser(true)
  override lazy val fixtures = Seq(userFixture)
}