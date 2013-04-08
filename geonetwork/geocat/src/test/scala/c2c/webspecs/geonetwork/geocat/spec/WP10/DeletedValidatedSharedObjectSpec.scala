package c2c.webspecs
package geonetwork
package geocat
package spec.WP10

import shared._
import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DeletedValidatedSharedObjectSpec extends DeletedNonValidatedSharedObjectSpec { override def is =
  "Delete Validated Shared Object Specification".title           ^ Step(setup) ^ 
  "This specification declares the behaviour of deleting a shared object"                   ^ 
      "First create several shared objects"                                                 ^ Step(CreateNonValidatedObjects) ^
      "Then import a metadata and add the shared objects to that Metadata"                  ^ Step(createMetadata) ^ bt ^
      "Validate the shared objects"                                                         ^ Step(validateSharedObjects) ^ bt ^
      ("Verify the objects are now validated by searching for all non-validated " +
      		"and verifying the validated objects are no longer there")                      ! noLongerNonValidated ^
      "Verify that all xlinks in MD were updated to be valid"                               ! allXLinksValidated ^
      "Verify that the index has been updated so the invalid xlink is no longer in the index"! indexIsCorrect ^
      ("Deleting the contact via the normal delete contact API " +
            "will move the contact to deleted object table and will update the" +
            "metadata with the new xlink")                                                  ! deleteContact ^  
      ("Deleting the extent via the normal delete contact API " +
            "will move the contact to deleted object table and will update the" +
            "metadata with the new xlink")                                                  ! deleteExtent(true) ^  
      ("Deleting the format via the normal delete contact API " +
            "will move the contact to deleted object table and will update the" +
            "metadata with the new xlink")                                                  ! deleteFormat ^  
      ("Deleting the keyword via the normal delete contact API " +
            "will move the contact to deleted object table and will update the" +
            "metadata with the new xlink")                                                  ! deleteKeyword(true) ^  
                                                                                              Step(tearDown)

  override def doFindSharedContact = {
    val list = GeocatListUsers.execute(contactSpec.uuid.toString)
    list.value.headOption.map { contact =>
      new {
        def id = contact.userId
        def objType = SharedObjectTypes.contacts
      }
    }
  }
  /*override def doFindSharedKeyword = {
    val words = SearchKeywords(GeocatConstants.GEOCAT_THESAURUS :: Nil)(keywordSpec.uuid.toString).value
    words.headOption map { word =>
      new {
        def id = word.id
        def objType = SharedObjectTypes.keywords
      }
    }
  }*/
  override def doFindSharedFormat = {
    val formats = ListFormats.execute(formatSpec.uuid.toString).value
    formats.headOption map { format =>
      new {
        def id = format.id.toString
        def objType = SharedObjectTypes.formats
      }
    }
  }
  override def doFindSharedExtent = {
    val extents = SearchExtent(format = ExtentFormat.gmd_bbox, typeName = List(Extents.Validated)).execute(extentSpec.uuid.toString)
    extents.value.headOption map { extent =>
      new {
        def id = extent.id
        def objType = SharedObjectTypes.extents
      }
    }
  }
  def validateSharedObjects = {
    val contact = super.findSharedContact.get
    val validateContactResult = ValidateSharedObject(contact.id, contact.objType).execute()
    val format = super.findSharedFormat.get
    val validateFormatResult = ValidateSharedObject(format.id, format.objType).execute()
    val extent = super.findSharedExtent.get
    val validateExtentResult = ValidateSharedObject(extent.id, extent.objType).execute()
    val keyword = super.findSharedKeyword.get
    val validateKeywordResult = ValidateSharedObject(keyword.id, keyword.objType).execute()

    (validateContactResult must haveA200ResponseCode) and
    (validateExtentResult must haveA200ResponseCode) and
    (validateFormatResult must haveA200ResponseCode) and
    (validateKeywordResult must haveA200ResponseCode)
 }

  def noLongerNonValidated = {
    (ListNonValidatedContacts.execute().value.find(_.description contains contactSpec.uuid.toString()) must beNone) and
        (ListNonValidatedExtents.execute().value.find(_.description contains extentSpec.uuid.toString()) must beNone) and
        (ListNonValidatedFormats.execute().value.find(_.description contains formatSpec.uuid.toString()) must beNone) and
        (ListNonValidatedKeywords.execute().value.find(_.description contains keywordSpec.uuid.toString()) must beNone)
  }
  
  def indexIsCorrect = {
    (ListReferencingMetadata(findSharedContact.get.id, SharedObjectTypes.contacts).execute().value must beEmpty) and
        (ListReferencingMetadata(findSharedExtent.get.id, SharedObjectTypes.extents).execute().value must beEmpty) and
        (ListReferencingMetadata(findSharedFormat.get.id, SharedObjectTypes.formats).execute().value must beEmpty) and 
        (ListReferencingMetadata(findSharedKeyword.get.id, SharedObjectTypes.keywords).execute().value must beEmpty)
  }
  def allXLinksValidated = {
    val xlinks = getMetadataWithXLinks.getXml \\ "_" filter { _ @@ "xlink:href" nonEmpty}
    val role = xlinks map { _ @@ "xlink:role".trim() }
    
    (role must not beEmpty) and
        (role.filter(_.nonEmpty) must beEmpty)
  }
}