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
class `SaveConfigurationSpec` extends GeocatSeleniumSpecification with ThrownExpectations { 

  def isImpl = 
  "This specification tests SaveConfiguration"    ^ 
    "The selenium script should succeed"                                        ! scala_specs2_1^
    "- Open the system configuration page"                                      ! scala_specs2_2^
    "- for testing purposes set maxrecords to be 9999"                          ! scala_specs2_3^
    "- save the configuration"                                                  ! scala_specs2_4^
    "- reload the configuration page"                                           ! scala_specs2_5^
    "- verify the max records value is 9999"                                    ! scala_specs2_6^
    "- reset to previous value and save"                                        ! scala_specs2_7

  def scala_specs2_1 = {
    import selenium._
    open("/geonetwork/srv/eng/geocat")
    `type`("id=username", adminUser)
    `type`("id=password", adminPass)
    click("css=button.banner")
    waitForPageToLoad("30000")
    success
  }

  def scala_specs2_2 = {
    import selenium._
    open("geonetwork/srv/eng/config")
    waitForPageToLoad("30000")
    success
  }

  var maxRecords = "1000"
    
  def scala_specs2_3 = {
    import selenium._
    maxRecords = getValue("id=selection.maxrecords")
    `type`("id=selection.maxrecords", "9999")
    "9999" must_== getValue("id=selection.maxrecords")
  }

  def scala_specs2_4 = {
    import selenium._
    click("//button[@onclick='config.save()']")
    "9999" must_== getValue("id=selection.maxrecords")
  }

  def scala_specs2_5 = {
    import selenium._
    open("geonetwork/srv/eng/config")
    waitForPageToLoad("30000")
    success
  }

  def scala_specs2_6 = {
    import selenium._
    "9999" must_== getValue("id=selection.maxrecords")
  }

  def scala_specs2_7 = {
    import selenium._
    `type`("id=selection.maxrecords", maxRecords)
    click("//button[@onclick='config.save()']")
    maxRecords must_== getValue("id=selection.maxrecords")
  }

  val TIMEOUT = 30
  private def doWait(assertion: => Boolean) = 
    (1 to TIMEOUT).view map {_=> sleep(1000)} find { _ => assertion }

}
