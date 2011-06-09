package selenium

import org.specs2._
import matcher.ThrownExpectations
import specification.Step
import org.openqa.selenium.WebDriverBackedSelenium

class Search_For_All_Metadata extends Specification with ThrownExpectations {

  lazy val selenium = new WebDriverBackedSelenium(new org.openqa.selenium.firefox.FirefoxDriver(), "http://www.geocat.ch/")

  def is =
  sequential                                                  ^
  "This specification tests Search_For_All_Metadata" ^ Step(() => selenium) ^
    "The selenium script should succeed"                    ! scala_specs2_1 ^
    "spec 1"                                                 ^
    "spec 2"                                                ! scala_specs2_2 ^
    "spec3"                                                 ! scala_specs2_3 ^
                                                            Step(selenium.stop()) ^
                                                            end


  def scala_specs2_1 = {
    import selenium._
    `open`("/geonetwork/srv/eng/geocat")
    `type`("username", "admin")
    // A normal comment
    `type`("password", "Hup9ieBe")
    `click`("css=td.x-btn-center")
    success
  }

  def scala_specs2_2 = {
    import selenium._
    `isTextPresent`("L'Atlas du territoire genevois") must beTrue
  }

  def scala_specs2_3 = {
    import selenium._
    // ``()
    `isElementPresent`("css=img[title=Genève (SITG)]") must beTrue
    // ``()
    success
  }
}
