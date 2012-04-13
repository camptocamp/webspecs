package c2c.webspecs
package geonetwork
package geocat
package spec
package WP10.selenium

import org.specs2._
import matcher.ThrownExpectations
import specification.Step
import Thread._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ViewNonValidatedObjectsSeleniumSpec extends GeocatSeleniumSpecification with ThrownExpectations { 

  def isImpl = 
  "This specification tests Viewing all the NonValidated Objects"    ^ Step(importMd) ^
    "First the browser must login"                                              ! scala_specs2_1^
    "- Verify that the imported user exists as a nonValidated user"             ! scala_specs2_2^
    "- Verify that the imported format exists as a nonValidated format"         ! scala_specs2_3^
    "- Verify that the imported keyword exists as a nonValidated keyword"       ! scala_specs2_4 


  def importMd = {
    config.adminLogin.execute()
    val importResponse = ImportMetadata.defaults(uuid, "/geocat/data/comprehensive-iso19139che.xml", false, getClass)._2.execute()
    registerNewMd(Id(importResponse.value.id))
    importResponse must haveA200ResponseCode
  }
  
  def scala_specs2_1 = {
    import selenium._
    open("/geonetwork/srv/eng/geocat")
    `type`("id=username", adminUser)
    `type`("id=password", adminPass)
    click("css=button.banner")
    waitForPageToLoad("30000")
    open("/geonetwork/srv/eng/reusable.non_validated.admin")
    success
  }

  def scala_specs2_2 = {
    import selenium._
    (isTextPresent("{automated_test_metadata}3 {automated_test_metadata}3 <"+uuid+"@c2c.com>") must beTrue.eventually(10,1.second)) 
  }

  def scala_specs2_3 = {
    import selenium._
    click("link=Formats")
    (isTextPresent(""+uuid+"Format") must beTrue.eventually(10,1.second)) and
        (isTextPresent(""+uuid+"Format2") must beTrue.eventually(10,1.second)) 
  }

  def scala_specs2_4 = {
    import selenium._
    click("link=Keywords")
    (isTextPresent(uuid+"EN") must beTrue.eventually(10,1.second)) 
  }

  val TIMEOUT = 30
  private def doWait(assertion: => Boolean) = 
    (1 to TIMEOUT).view map {_=> sleep(1000)} find { _ => assertion }

}
