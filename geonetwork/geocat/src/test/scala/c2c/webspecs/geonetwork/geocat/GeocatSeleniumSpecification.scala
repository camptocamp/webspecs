package c2c.webspecs
package geonetwork
package geocat

import org.specs2.specification.Step
import org.openqa.selenium.WebDriverBackedSelenium
import org.specs2.specification.Fragments
import org.openqa.selenium.WebDriver

abstract class GeocatSeleniumSpecification extends GeocatSpecification {
  lazy val driver:WebDriver = new org.openqa.selenium.firefox.FirefoxDriver()
  lazy val selenium = new WebDriverBackedSelenium(driver, "http://" + Properties.testServer)

  lazy val adminUser = Properties(config.ADMIN_USER_KEY).get 
  lazy val adminPass = Properties(config.ADMIN_USER_PASS).get

  val eventuallyBeTrue = beTrue.eventually(10,1.second)
  def clickSearch() = selenium.click("//table[@id='searchBt']//button")
  def is = sequential ^ Step(setup) ^ isImpl ^ Step(tearDown)

  def isImpl: Fragments

  override def extraSetup(setupContext: ExecutionContext): Unit = {
    super.extraSetup(context)
    selenium
  }

    override def extraTeardown(teardownContext:ExecutionContext):Unit = {
      super.extraTeardown(teardownContext)
      selenium.stop()
    }

}