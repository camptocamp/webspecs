package c2c.webspecs
package geonetwork
package geocat
package spec.WP15

import org.specs2.specification.Step
import c2c.webspecs.geonetwork.edit._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml.NodeSeq
import scala.xml.Node

@RunWith(classOf[JUnitRunner])
class UpdateContactViaMetadataUpdate extends GeocatSpecification { def is =
  "Metadata Edit/Update Contact".title ^
  "This spec tests what happens when a contact is updated in a metadata that has multiple contacts and the same contact is present multiple times" ^ Step(setup) ^
  "Open a metadata for editing and edit a contact" ^ Step(updatedMetadata) ^
  "Verify that contact is updated" ! contactIsUpdated ^
  "Verify that all the contacts are still present" ! allContactsPresent ^
  "Verify that the same shared contact has been updated as well" ! sharedContactsUpdated ^
                                                                  Step(tearDown)
                                                                  

  def text(firstNameNode:Node) = (firstNameNode \\ "CharacterString").text.trim
  def findNamesWith(xml:NodeSeq, prefix:String) = {
    xml \\ "individualFirstName" filter {n => text(n) startsWith prefix}
  }
  lazy val updatedMetadata = {
    val id = importMd(1,"/geocat/data/contact_has_repeated_contact.xml", uuid.toString(), GeocatConstants.GM03_2_TO_CHE_STYLESHEET)
    val editValue = StartEditing().execute(id.head).value
    val request = findNamesWith(editValue.getXml,"firstname").headOption map {elem =>
      val ref = (elem \ "CharacterString" \ "element" head) @@ "ref"
      val updateResponse = UpdateMetadata("_"+ref.head -> ("newName"+uuid)).execute(editValue)
      assert(updateResponse.basicValue.responseCode == 200, "Response code for metadata update was not 200 instead: "+updateResponse.basicValue.responseCode+"\n\tmsg: "+updateResponse.basicValue.responseMessage)
      updateResponse.value.getXml
    }
    
    assert(request.isDefined, "individual first name was not found!")
    
    request.get
  }
  def contactIsUpdated = {
    val updatedContact = findNamesWith(updatedMetadata,"newName").headOption.map(text)
    
    updatedContact must beSome("newName"+uuid)
  }
  def allContactsPresent = {
    updatedMetadata \\ "individualFirstName" must haveSize (3)
  }
  
  def sharedContactsUpdated = 
    (findNamesWith(updatedMetadata,"firstname") must beEmpty) and
       (findNamesWith(updatedMetadata, "newName") must haveSize(2))
  
}