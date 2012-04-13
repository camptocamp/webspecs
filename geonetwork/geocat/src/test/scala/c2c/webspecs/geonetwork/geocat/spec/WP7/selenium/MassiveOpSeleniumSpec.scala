package c2c.webspecs
package geonetwork
package geocat
package spec
package WP7.selenium

import org.specs2._
import matcher.ThrownExpectations
import specification.Step
import Thread._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MassiveOpSeleniumSpec extends GeocatSeleniumSpecification with ThrownExpectations { 

  def isImpl = 
  "This specification tests MassiveOpSeleniumSpec"                                  ^ Step(importMd(3, identifier=datestamp)) ^ 
                                                                                      Step(correctResults(3, identifier=datestamp)) ^
    "This spec tests the functionality of massive operations from a UI perspective" ! scala_specs2_1^
    "perform a search for the imported metadata"                                    ! scala_specs2_2^
    "select all metadata"                                                           ! scala_specs2_3^
    "verify that actions on selection combo is enabled"                             ! scala_specs2_4^
    "select update privileges"                                                      ! scala_specs2_5^
    "select all for 'All' 'Intranet' and 'Guest'"                                   ! scala_specs2_6^
    "logout and verify that the metadata are present as expected"                   ! scala_specs2_7^
    "log back in, perform search and verify the metadata are also in that view"     ! scala_specs2_8^
    "reset privileges (so no longer published"                                      ! scala_specs2_9^
    "logout again and verify they are correctly unpublished"                        ! scala_specs2_10

  def scala_specs2_1 = {
    import selenium._
    open("/geonetwork/srv/eng/geocat")
    `type`("id=username", adminUser)
    `type`("id=password", adminPass)
    click("id=loginButton")
    waitForPageToLoad("30000")
    success
  }

  def scala_specs2_2 = {
    import selenium._
    `type`("id=anyField", "Title"+datestamp)
    clickSearch()
    doWait(isElementPresent("link=EN Title"+datestamp))
    isElementPresent("link=EN Title"+datestamp) must beTrue
  }

  def scala_specs2_3 = {
    import selenium._
    click("link=all")
    success
  }

  def scala_specs2_4 = {
    import selenium._
    isEditable("id=actionOnSelection") must beTrue
  }

  def scala_specs2_5 = {
    import selenium._
    select("id=actionOnSelection", "label=Update privileges")
    doWait(isElementPresent("id=privileges"))
    click("css=button.content")
    success
  }

  def scala_specs2_6 = {
    import selenium._
    click("//button[@onclick=\"setAll('row.0'); return false;\"]")
    click("//button[@onclick=\"setAll('row.-1'); return false;\"]")
    click("//div[@id='privileges']//button[contains(string(@onclick),'checkBoxModalUpdate')]")
    doWait(false, 1, 1000)
    success
  }

  def scala_specs2_7 = {
    import selenium._
    click("id=logoutButton")
    waitForPageToLoad("30000")
    `type`("id=anyField", "Title"+datestamp)
    clickSearch()
    doWait(isElementPresent("link=EN Title"+datestamp))
    isElementPresent("link=EN Title"+datestamp) must beTrue
  }

  def scala_specs2_8 = {
    import selenium._
    `type`("id=username", adminUser)
    `type`("id=password", adminPass)
    click("id=loginButton")
    waitForPageToLoad("30000")
    `type`("id=anyField", "Title"+datestamp)
    click("css=td.x-btn-center")
    doWait(isElementPresent("link=EN Title"+datestamp))
    isElementPresent("link=EN Title"+datestamp) must beTrue
  }

  def scala_specs2_9 = {
    import selenium._
    click("link=all")
    select("id=actionOnSelection", "label=Update privileges")
    doWait(isElementPresent("id=privileges"))
    click("//tr[./td/span/text() = '"+config.user+"']//button[contains(string(@onclick),'setAll')]")
    click("//div[@id='privileges']//button[contains(string(@onclick),'checkBoxModalUpdate')]")
    doWait(false, 1, 1000)
    success
  }

  def scala_specs2_10 = {
    import selenium._
    click("id=logoutButton")
    waitForPageToLoad("30000")
    `type`("id=username", adminUser)
    `type`("id=password", adminPass)
    `type`("id=anyField", "Title"+datestamp)
    click("css=td.x-btn-center")
    doWait(false, 1, 1000)
    isElementPresent("link=EN Title"+datestamp) must beFalse
  }

  val TIMEOUT = 30
  private def doWait(assertion: => Boolean, waits:Int=TIMEOUT, length:Int=1000) = 
    (1 to waits).view map {_=> sleep(length)} find { _ => assertion }

}
