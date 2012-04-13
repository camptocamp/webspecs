package c2c.webspecs
package geonetwork
package geocat
package spec
package WP15.selenium

import org.specs2._
import matcher.ThrownExpectations
import specification.Step
import Thread._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EditContactSpec extends GeocatSeleniumSpecification with ThrownExpectations { 

  def isImpl = 
  "This specification tests Editting Contacts "                                 ^ Step(ImportMetadata) ^
    "Editing Contact updates reusable object"                                   ^
    "open metadata and verify that there are the 3 expected contacts"           ^ openMetadata ^
    "edit metadata and update firstname of contact"                             ! updateMetadata ^
    "verify that there reusable object has been updated"                        ! reusableUpdated^
    "verify that there are still 3 contacts in metadata"                        ! hasThreeContacts

  lazy val ImportMetadata = {
    val mdId = importMd(1,"/geocat/data/contact_has_repeated_contact.xml", uuid.toString, GeocatConstants.GM03_2_TO_CHE_STYLESHEET).head
    mdId
  }

  def openMetadata = {
    import selenium._
    seleniumLogin()
    
    open("/geonetwork/srv/eng/metadata.edit?id="+ImportMetadata)
    (getEval("Ext.query('input[value=firstname"+uuid+"]').length").toInt must_== 2) and
        (getEval("Ext.query('input[value=ff1"+uuid+"]').length").toInt must_== 1)
  }

  def updateMetadata = {
    import selenium._
    val id = selenium.getEval("Ext.query('input[value=firstname"+uuid+"]')[0].id")
    `type`("id="+id, "newname"+uuid)
    click("id=btnSave")
    doWait(isTextPresent("Point of contact"))
    success
  }
  def reusableUpdated = {
    import selenium._
    getEval("Ext.query('input[value=newname"+uuid+"]').length").toInt must_== 2 
  }

  def hasThreeContacts = {
    import selenium._
    (getEval("Ext.query('input[value=newname"+uuid+"]').length").toInt must_== 2) and
        (getEval("Ext.query('input[value=ff1"+uuid+"]').length").toInt must_== 1)
  }

  val TIMEOUT = 30
  private def doWait(assertion: => Boolean, waits:Int=TIMEOUT, length:Int=1000) = 
    (1 to waits).view map {_=> sleep(length)} find { _ => assertion }

}
