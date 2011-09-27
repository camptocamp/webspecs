package c2c.webspecs
package geonetwork
package geocat
package spec
package WP7.selenium

import org.specs2._
import matcher.ThrownExpectations
import specification.Step
import Thread._
import org.openqa.selenium.WebDriverBackedSelenium
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SearchesReturnResultsSeleniumSpec extends GeocatSeleniumSpecification with ThrownExpectations { 
    override lazy val selenium = new WebDriverBackedSelenium(driver, "http://" + Properties.testServer)
  
  def isImpl = 
  "This specification tests SearchesReturnResultsSeleniumSpec"                  ^ Step(importMd(4, identifier=datestamp)) ^ 
                                                                                  Step(correctResults(4, datestamp)) ^
    "This spec runs several searches then modifies the ordering and verifies that a result still completes."! scala_specs2_1^
    "sortBy date and ensure that results are correctly displayed"               ! scala_specs2_2^
    "sortBy populatiry and ensure that results are correctly displayed"         ! scala_specs2_3^
    "sortBy rating and ensure that results are correctly displayed"             ! scala_specs2_4^
    "sortBy relevance and ensure that results are correctly displayed"          ! scala_specs2_5^
    "sortBy title and ensure that results are correctly displayed"              ! scala_specs2_6

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
    clickSearch()
    doWait(isElementPresent("//div[@id='sortBy']//img"))
    click("//div[@id='sortBy']//img")
    click("css=div.x-combo-list-item")
    doWait(isElementPresent("//div[@id='records']/table/tbody/tr/td[2]"))
    (isElementPresent("//div[@id='records']/table/tbody/tr/td[2]") must beTrue) and
        ("Change date" must beEqualTo(getValue("id=sortByCombo")).ignoreCase)
  }

  def scala_specs2_3 = {
    import selenium._
    refresh()
    waitForPageToLoad("30000")
    clickSearch()
    doWait(isElementPresent("//div[@id='sortBy']//img"))
    click("//div[@id='sortBy']//img")
    click("css=div.x-combo-list-item + div")
    doWait(isElementPresent("//div[@id='records']/table/tbody/tr/td[2]"))
    (isElementPresent("//div[@id='records']/table/tbody/tr/td[2]") must beTrue) and
        ("Popularity" must beEqualTo(getValue("id=sortByCombo")).ignoreCase)
  }

  def scala_specs2_4 = {
    import selenium._
    refresh()
    waitForPageToLoad("30000")
    clickSearch()
    doWait(isElementPresent("//div[@id='sortBy']//img"))
    click("//div[@id='sortBy']//img")
    click("css=div.x-combo-list-item + div + div")
    doWait(isElementPresent("//div[@id='records']/table/tbody/tr/td[2]"))
    (isElementPresent("//div[@id='records']/table/tbody/tr/td[2]") must beTrue) and
        ("Rating" must beEqualTo(getValue("id=sortByCombo")).ignoreCase)
  }

  def scala_specs2_5 = {
    import selenium._
    refresh()
    waitForPageToLoad("30000")
    clickSearch()
    doWait(isElementPresent("//div[@id='sortBy']//img"))
    click("//div[@id='sortBy']//img")
    click("css=div.x-combo-list-item + div + div + div")
    doWait(isElementPresent("//div[@id='records']/table/tbody/tr/td[2]"))
    (isElementPresent("//div[@id='records']/table/tbody/tr/td[2]") must beTrue) and
        ("Relevance" must beEqualTo(getValue("id=sortByCombo")).ignoreCase)
  }

  def scala_specs2_6 = {
    import selenium._
    refresh()
    waitForPageToLoad("30000")
    clickSearch()
    doWait(isElementPresent("//div[@id='sortBy']//img"))
    click("//div[@id='sortBy']//img")
    click("css=div.x-combo-list-item + div + div + div + div")
    doWait(isElementPresent("//div[@id='records']/table/tbody/tr/td[2]"))
    (isElementPresent("//div[@id='records']/table/tbody/tr/td[2]") must beTrue) and
        ("Title" must beEqualTo(getValue("id=sortByCombo")).ignoreCase)
  }

  val TIMEOUT = 30
  private def doWait(assertion: => Boolean, waits:Int=TIMEOUT, length:Int=1000) = 
    (1 to waits).view map {_=> sleep(length)} find { _ => assertion }

}
