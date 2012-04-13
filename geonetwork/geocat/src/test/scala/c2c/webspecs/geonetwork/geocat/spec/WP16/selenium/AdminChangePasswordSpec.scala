package c2c.webspecs
package geonetwork
package geocat
package spec
package WP16.selenium

import org.specs2._
import matcher.ThrownExpectations
import Thread._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AdminChangePasswordSpec extends GeocatSeleniumSpecification with ThrownExpectations { 

  def isImpl = 
  "This specification tests AdminChangePasswordSpec"    ^ 
    "This Specification tests changing the administrator's username and password"! scala_specs2_1^
    "login as admin"                                                            ! scala_specs2_2^
    "Navigate to change password page"                                          ! scala_specs2_3^
    "change password"                                                           ! scala_specs2_4^
    "Logout and log back in with new password"                                  ! scala_specs2_5^
    "verify that user is admin"                                                 ! scala_specs2_6^
    "Change password back"                                                      ! scala_specs2_7^
    "Log back out and verify that original password works again"                ! scala_specs2_8

  def scala_specs2_1 = {
    import selenium._
    open("/geonetwork/srv/eng/geocat")
    success
  }

  def scala_specs2_2 = {
    import selenium._
    `type`("id=username", adminUser)
    `type`("id=password", adminPass)
    click("css=button.banner")
    waitForPageToLoad("30000")
    isElementPresent("link=Metadata management") must eventuallyBeTrue
  }

  def scala_specs2_3 = {
    import selenium._
    click("link=Metadata management")
    waitForPageToLoad("30000")
    click("link=Change password")
    waitForPageToLoad("30000")
    success
  }

  def scala_specs2_4 = {
    import selenium._
    `type`("name=password", adminPass)
    `type`("name=newPassword", "newpassword")
    `type`("name=newPassword2", "newpassword")
    click("//button[@onclick='doUpdate()']")
    waitForPageToLoad("30000")
    isTextPresent("Your password has been successfully changed") must eventuallyBeTrue
  }

  def scala_specs2_5 = {
    import selenium._
    click("css=button.banner")
    waitForPageToLoad("30000")
    `type`("id=username", adminUser)
    `type`("id=password", "newpassword")
    click("css=button.banner")
    waitForPageToLoad("30000")
    success
  }

  def scala_specs2_6 = {
    import selenium._
    (isTextPresent("User: admin admin") must eventuallyBeTrue) and
        (isElementPresent("link=Metadata management") must eventuallyBeTrue)
  }

  def scala_specs2_7 = {
    import selenium._
    click("link=Metadata management")
    waitForPageToLoad("30000")
    click("link=Change password")
    waitForPageToLoad("30000")
    `type`("name=password", "newpassword")
    `type`("name=newPassword", adminPass)
    `type`("name=newPassword2", adminPass)
    click("//button[@onclick='doUpdate()']")
    waitForPageToLoad("30000")
    isTextPresent("Your password has been successfully changed") must eventuallyBeTrue
  }

  def scala_specs2_8 = {
    import selenium._
    click("css=button.banner")
    waitForPageToLoad("30000")
    `type`("id=username", adminUser)
    `type`("id=password", adminPass)
    click("css=button.banner")
    waitForPageToLoad("30000")
    (isTextPresent("User: admin admin") must eventuallyBeTrue) and
        (isElementPresent("link=Metadata management") must eventuallyBeTrue)
  }

  val TIMEOUT = 30
  private def doWait(assertion: => Boolean) = 
    (1 to TIMEOUT).view map {_=> sleep(1000)} find { _ => assertion }
    
}
