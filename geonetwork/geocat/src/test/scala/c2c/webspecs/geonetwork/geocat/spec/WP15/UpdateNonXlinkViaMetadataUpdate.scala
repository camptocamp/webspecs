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
class UpdateNonXlinkViaMetadataUpdate extends GeocatSpecification { def is =
  "Metadata Edit/Update Non XLink".title ^
  "This spec verifies that editing a 'normal' metadata element in a page with multiple xlinks works as expected" ^ Step(setup) ^
  "Open a metadata for editing and edit the title" ^ Step(updatedMetadata) ^
  "Verify that the title is updated " ! titleIsUpdated ^
  Step(tearDown)
                                                                  

  def text(firstNameNode:Node) = (firstNameNode \\ "LocalisedCharacterString").text.trim
  def findNamesWith(xml:NodeSeq, prefix:String) = {
    xml \\ "title" filter {n => text(n) startsWith prefix}
  }

  val newTitle = "NewTitle"+uuid
  lazy val updatedMetadata = {
    val id = importMd(1,"/geocat/data/contact_has_repeated_contact.xml", uuid.toString(), GeocatConstants.GM03_2_TO_CHE_STYLESHEET)
    val editValue = StartEditing().execute(id.head).value
    
    val request = findNamesWith(editValue.getXml,"EXPLOITATION DES GRAVIERES").headOption map {elem =>
      val ref = (elem \\ "LocalisedCharacterString" \ "element" head) @@ "ref"
      val updateResponse = UpdateMetadata("_"+ref.head -> (newTitle)).execute(editValue)
      assert(updateResponse.basicValue.responseCode == 200, "Response code for metadata update was not 200 instead: "+updateResponse.basicValue.responseCode+"\n\tmsg: "+updateResponse.basicValue.responseMessage)
      updateResponse.value.getXml
    }
    
    assert(request.isDefined, "title was not found!")
    
    request.get
  }
  def titleIsUpdated = {
    val updatedTitle = findNamesWith(updatedMetadata,newTitle).headOption.map(text)
    val oldTitle = findNamesWith(updatedMetadata,"EXPLOITATION DES GRAVIERES").headOption
    (oldTitle must beNone) and (updatedTitle must beSome(newTitle))
  } 
}