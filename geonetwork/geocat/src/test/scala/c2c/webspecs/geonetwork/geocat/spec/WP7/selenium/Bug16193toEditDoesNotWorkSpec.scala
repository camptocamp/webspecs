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
class Bug16193toEditDoesNotWorkSpec extends GeocatSeleniumSpecification with ThrownExpectations { 

  def isImpl = 
  "This specification tests Bug16193toEditDoesNotWorkSpec"                          ^ Step(importedMdId) ^
    "this spec test that toEdit and toPublish return metadata records for editing and publishing"! scala_specs2_1^
    "Import a metadata as a user and then switch to the advanced search form"       ! scala_specs2_2^
    "Check that the metadata can be found with a normal search"                     ! checkCanFind ^
    "check toEdit and then perform a search.  The imported metadata should appear"  ! checkToEdit^
    "uncheck toEdit and check toPublish.  The MD should be there"                   ! checkToPublish ^
    "Publish imported Metadata"                                                     ^ Step(publish) ^
    "Publish MD and verify that it no longer shows up in toPublish"                 ! checkNotToPublish ^
                                                                                      Step(selenium.click("//div[./input/@id='toPublish']")) ^
    "Publish MD and verify that it still shows up with toEdit"                      ! checkToEdit

  lazy val importedMdId = importMd(1, identifier=datestamp)
  def scala_specs2_1 = {
    import selenium._
    open("/geonetwork/srv/eng/geocat")
    doWait(isElementPresent("id=username"))
    `type`("id=username", config.user)
    `type`("id=password", config.pass)
    click("id=loginButton")
    waitForPageToLoad("30000")
    success
  }

  def scala_specs2_2 = {
    import selenium._
    click("link=Advanced")
    success
  }
  
  def checkCanFind = {
    import selenium._
    `type`("id=TitleField", "Title"+datestamp)
    clickSearch()
    doWait(isElementPresent("link=EN Title"+datestamp))
    isElementPresent("link=EN Title"+datestamp) must beTrue
  }

  def checkToEdit = {
    import selenium._
    `type`("id=TitleField", "Title"+datestamp)
    click("//div[./input/@id='toEdit']")
    clickSearch()
    doWait(isElementPresent("link=EN Title"+datestamp))
    isElementPresent("link=EN Title"+datestamp) must beTrue
  }

  def checkToPublish = {
    import selenium._
    `type`("id=TitleField", "Title"+datestamp)
    click("//div[./input/@id='toEdit']")
    click("//div[./input/@id='toPublish']")
    clickSearch()
    doWait(false, 1,200)
    doWait(isElementPresent("link=EN Title"+datestamp))
    isElementPresent("link=EN Title"+datestamp) must beTrue
  }
  
  def publish = {
    val mdId = importedMdId(0)
    config.adminLogin.execute()
    GetRequest("metadata.admin","id" -> mdId, "_1_0" -> "on").execute()
  }
  
  def checkNotToPublish = {
    import selenium._
    clickSearch()
    doWait(false, 1,200)
    doWait(!isElementPresent("css=div.searching"))
    isElementPresent("link=EN Title"+datestamp) must beFalse
  }
  

  val TIMEOUT = 30
  private def doWait(assertion: => Boolean, waits:Int=TIMEOUT, length:Int=1000) = 
    (1 to waits).view map {_=> sleep(length)} find { _ => assertion }

}
