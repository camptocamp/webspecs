package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step
import c2c.webspecs.{XmlValue, Response}
import geonetwork.UserRef

class AccessContacts extends GeonetworkSpecification { def is =
  "This specification tests accessing shared users"    ^ Step(setup) ^
    "Listing all users"                                ^ listContacts.give ^
      "Should suceed with a 200 response"                ^ l200Response ^
      "Should show user name"                            ^ listUserNames.then    ^
      "Should have email"                                ^ listEmails.then   ^
      "Should have email"                                ^ listNames.then   ^
      "Should have prifle"                               ^ listProfile.then   ^
                                                         end ^
    "Gettings a user in iso xml"                       ^ formatInIso.give  ^
      "Should suceed with a 200 response"                ^ i200Response      ^
      "Should show user name"                            ^ isoName.then      ^
      "Should have email"                                ^ isoVersion.then   ^
                                                           Step(tearDown)

  type ListUserResponse = Response[List[User with UserRef]]

  val listContacts = (_:String) => ListUsers(None):ListUserResponse
  val l200Response = a200ResponseThen.narrow[ListUserResponse]
  val listUserNames = (response:ListUserResponse, _:String) => {
    response.value.map{_.username} must contain (userFixture.username)
  }
  val listNames = (response:ListUserResponse, _:String) => {
    response.value.map{_.name} must contain (userFixture.name)
  }
  val listEmails = (response:ListUserResponse, _:String) => {
    response.value.map{_.email} must contain (userFixture.email)
  }
  val listProfile = (response:ListUserResponse, _:String) => {
    val ourContact = response.value.find(_.userId == userFixture.id)
    ourContact.map{_.profile} must beSome(SharedUser)
  }

  val formatInIso = (s:String) => (GetRequest("xml.user.get","id" -> userFixture.id)(None)):Response[XmlValue]
  val i200Response = a200ResponseThen.narrow[Response[XmlValue]]
  val isoName = (r:Response[XmlValue], _:String) => pending/*
    r.value.withXml { xml =>
      val nameElems = xml \\ "name"
      ( (nameElems must haveSize (1)) and
        (nameElems.head.text.trim must_== userFixture.name)
      )
    }                                                        */
  val isoVersion = (r:Response[XmlValue], _:String) => pending /*
    r.value.withXml { xml =>
      val nameElems = xml \\ "version"
      ( (nameElems must haveSize (1)) and
        (nameElems.head.text.trim must_== userFixture.version)
      )
    }                                                            */

  def noFormat = pending /*
    ExecutionContext.withDefault{c =>
      val response = GetRequest("xml.format.get!","id" -> userFixture.id)(None)(c).value
      response.withXml(_ \\ "record" must beEmpty)
    }
*/
  lazy val userFixture = GeonetworkFixture.user(SharedUser)
  override lazy val fixtures = Seq(userFixture)
}