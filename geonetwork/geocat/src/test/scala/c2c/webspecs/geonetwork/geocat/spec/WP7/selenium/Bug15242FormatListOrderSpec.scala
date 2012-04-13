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
import org.openqa.selenium.firefox.FirefoxDriver
import csw._

@RunWith(classOf[JUnitRunner])
class Bug15242FormatListOrderSpec extends GeocatSeleniumSpecification with ThrownExpectations { 

  def isImpl = 
  "This specification tests Bug15242FormatListOrderSpec"    ^ 
    "Import a couple metadata with a distinct format"                           ^ Step(importMd(2,"/geocat/data/metadata-validate-formats-spec.xml", identifier=datestamp)) ^ 
    "verify that a csw search for that format finds the format"                 ! search ^ 
    "This spec fixes the format ordering issue listed in Bug 15242"             ! scala_specs2_1^
    "Login and go to geocat search page"                                        ! scala_specs2_2^
    "switch to advanced view"                                                   ! scala_specs2_3^
    "expand format combo"                                                       ! scala_specs2_4^
    "assert that formats are sorted correctly"                                  ! scala_specs2_5
    
  def search = {
    val filter = PropertyIsEqualTo("format",datestamp.toString)
    val xml = CswGetRecordsRequest(filter.xml).execute().value.getXml
    
    (xml \\ "@numberOfRecordsMatched").text.toInt must_== 2
  }

  def scala_specs2_1 = {
    import selenium._
    open("/geonetwork/srv/eng/geocat")
    success
  }

  def scala_specs2_2 = {
    import selenium._
    `type`("id=username", adminUser)
    `type`("id=password", adminPass)
    click("id=loginButton")
    waitForPageToLoad("30000")
    success
  }

  def scala_specs2_3 = {
    import selenium._
    click("link=Advanced")
    doWait(isElementPresent("id=formatCombo"),10,500)
    success
  }

  def scala_specs2_4 = {
    import selenium._
    focus("id=formatCombo")
    doWait(false,1,500)
    click("id=formatCombo")
    doWait(isElementPresent("css=.x-combo-list-item"),1,1000)
    success
  }

  def scala_specs2_5 = {
    val selectText = driver match {
      case _:FirefoxDriver => "innerHTML"
      case _ => "innerText"
    }
    val formatsJs = (""" 
               |var elems = Ext.select(".x-combo-list-item").elements;
               |var result = "";
               |for(i in elems) { if (elems.hasOwnProperty(i)) {result += "∫∫"+elems[i]."""+selectText+""";}}
               |result;
               |""").stripMargin.trim
    val formatsString = selenium.getEval(formatsJs)
    val formats = formatsString.split("∫∫").toList.map(_.trim()).filter(_.nonEmpty)
    val values = formats.sliding(2,1).toList.map{case List(a,b) => a.compareTo(b)}
    val expectedPercentageOfCorrect = if(values.size > 10) 0.1 else 0.25
    
    // I don't expect all to be less than because javascript compareTo is different than java's
    values.filter(_>0).size.toDouble/values.size must be_< (expectedPercentageOfCorrect)
  }

  val TIMEOUT = 30
  private def doWait(assertion: => Boolean, waits:Int=TIMEOUT, length:Int=1000) = 
    (1 to waits).view map {_=> sleep(length)} find { _ => assertion }

}
