package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import c2c.webspecs.geonetwork.geocat.GeocatSpecification
import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EscapeSpecialCharsInUserSpec extends GeocatSpecification { def is =
  "Escape Special Characters in User".title ^ Step(setup) ^ sequential ^
  "This spec ensures that special characters can be present in user data" ^ 
      "Create a user with & and < and and / in the name" ^ Step(createNewSharedUser) ^
      "Ensure that the users can still be listed with out errors" ! listUsers ^
      "Ensure that search for users works"                        ! searchUsers ^
      "Ensure that delete user works"                             ! deleteUser ^
                                                                    Step(tearDown)

  val wordWithSpecialChars = uuid+"&or&amp;/ < > </> % èüöÇ"
  lazy val createNewSharedUser = {
    config.adminLogin()
    val user = User(uuid, SharedUserProfile).copy(
      name = wordWithSpecialChars,
      position = LocalisedString(en = wordWithSpecialChars))
    CreateNonValidatedUser(user)().value
  }
  def listUsers = {
    val userRequest = GeocatListUsers("")
    (userRequest must haveA200ResponseCode) and
      (userRequest.value.find(_.userId == createNewSharedUser.userId) must beSome) and
      (userRequest.value.find(_.name == wordWithSpecialChars) must beSome)
  }
  def searchUsers = {
    val userRequest = GeocatListUsers(uuid.toString)
    (userRequest must haveA200ResponseCode) and
      (userRequest.value.find(_.userId == createNewSharedUser.userId) must beSome) and
      (userRequest.value.find(_.name == wordWithSpecialChars) must beSome)
  }
  def deleteUser = {
    DeleteSharedUser(createNewSharedUser.userId, true)()
    val userRequest = GeocatListUsers("")
    (userRequest must haveA200ResponseCode) and
      (userRequest.value.find(_.userId == createNewSharedUser.userId) must beNone)
  }
}