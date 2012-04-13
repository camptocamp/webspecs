package c2c.webspecs
package geonetwork
package geocat
package spec.WP10.selenium

import org.specs2._
import matcher.ThrownExpectations
import Thread._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class `CreateEditDeleteUserSeleniumSpec` extends GeocatSeleniumSpecification with ThrownExpectations { 

  def isImpl = 
    "This specification tests creating/editing/deleting a shared user through the user interface"! scala_specs2_1^
    "login as administrator to edit shared objects"                             ! scala_specs2_2^
    "navigation to admin and admin validated user"                              ! scala_specs2_3^
    "click new user button"                                                     ! scala_specs2_4^
    "enter in user information"                                                 ! scala_specs2_5^
    "Click on and the new user should be in the list of validated users"        ! scala_specs2_6^
    "click edit button"                                                         ! scala_specs2_7^
    "change surname"                                                            ! scala_specs2_8^
    "click the update button and the updated user should be displayed"          ! scala_specs2_9^
    "Click the delete button of the new user and the confirmation dialog should appear.  Confirming the deletion should result in the user being deleted"! scala_specs2_10


  def scala_specs2_1 = {
    import selenium._
    open("/geonetwork/srv/eng/geocat")
    success
  }

  def scala_specs2_2 = {
    import selenium._
    `type`("id=username", adminUser)
    `type`("id=password", adminPass)
    success
  }

  def scala_specs2_3 = {
    import selenium._
    click("css=button.banner")
    waitForPageToLoad("30000")
    open("/geonetwork/srv/eng/validated.shared.user.admin");
    waitForPageToLoad("30000")
    success
  }

  def scala_specs2_4 = {
    import selenium._
    click("//button[@onclick=\"load('/geonetwork/srv/eng/shared.user.edit?validated=y&operation=newuser')\"]")
    waitForPageToLoad("30000")
    success
  }

  def scala_specs2_5 = {
    import selenium._
    `type`("id=username", uuid.toString)
    `type`("id=surname", "newuser")
    `type`("id=name", "newuser")
    `type`("id=orgEN", "en")
    select("id=langSelectororg", "label=Deutsch")
    `type`("id=orgDE", "de")
    success
  }

  def scala_specs2_6 = {
    import selenium._
    click("//button[@onclick='update1()']")
    waitForPageToLoad("30000")
    isTextPresent("newuser") must beTrue
  }

  def scala_specs2_7 = {
    import selenium._
    click(button(1))
    waitForPageToLoad("30000")
    ("en" must_== getValue("id=orgEN")) and
    ("de" must_== getValue("id=orgDE"))
  }

  def scala_specs2_8 = {
    import selenium._
    `type`("id=surname", "newuser2")
    success
  }

  def scala_specs2_9 = {
    import selenium._
    click("//button[@onclick='update1()']")
    waitForPageToLoad("30000")
    isTextPresent("newuser2") must beTrue
  }

  def scala_specs2_10 = {
    import selenium._
    selenium.chooseOkOnNextConfirmation()
    click(button(2))
    waitForPageToLoad("30000")
    click("css=button.banner")
    isTextPresent("newuser2") must beFalse
  }
  
  def button(id:Int) = "//tr[td/text() = '"+uuid.toString+"']/td/button["+id+"]"

  val TIMEOUT = 30
  private def doWait(assertion: => Boolean) = 
    (1 to TIMEOUT).view map {_=> sleep(1000)} find { _ => assertion }

}
