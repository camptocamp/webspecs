package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import shared._
import c2c.webspecs.geonetwork.geocat.GeocatSpecification
import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import c2c.webspecs.geonetwork.edit.AddSites
import org.specs2.matcher.MustThrownMatchers
import c2c.webspecs.geonetwork.geocat.shared.CreateNonValidatedUser
import c2c.webspecs.geonetwork.geocat.shared.DeleteSharedUser

@RunWith(classOf[JUnitRunner]) 
class ValidateSharedObjectSpec extends GeocatSpecification with MustThrownMatchers { def is = 
  "Validate Shared Object spec".title 				^ Step(setup) ^
  "This specification tests validation of shared objects"													^
  "This section tests validating a shared object that is present in multiple Metadata"						^
    "First we need to Import the same metadata object twice"												^ Step(ImportTwoMetadata)  ^
    "At this point the xlink in both metadata should not be validated"										! nonValidatedContacts  ^ bt ^ 
    "Next validate the imported shared object, in this case a contact"										^ Step(validateContact) ^
    "Once done the xlink in both metadata should be updated to be validated"								! isValidatedContact    ^
    																										  endp ^
  "This section tests a couple corner cases of shared object validation"                                    ^ 
    "Allow creation and deletion of shared contacts"                                                        ! createAndDelete ^
    "Allow creation of shared format and validate it without interfering with others in metadata" 			! createAndValidateNoInterference ^
    "Allow creation of shared format and validate it without losing role information"             			! createAndValidateKeepRole ^
    																										  Step(tearDown)

  lazy val ImportTwoMetadata: (IdValue, IdValue) = {
    val importRequest = ImportMetadata.defaults(uuid, "/geocat/data/metadata.iso19139.che.xml", false, classOf[ValidateSharedObject], ImportStyleSheets.NONE)._2
    config.adminLogin.execute()
    val id1 = importRequest.execute().value
    val id2 = importRequest.execute().value
    registerNewMd(id1, id2)
    (id1, id2)
  }

  def loadMetadata = {
    val md1 = GetEditingMetadataXml.execute(ImportTwoMetadata._1).value.getXml
    val md2 = GetEditingMetadataXml.execute(ImportTwoMetadata._2).value.getXml

    val contact1 =  md1 \\ "contact" filter (n => (n \\ "electronicMailAddress").text.trim == uuid + "@c2c.com")
    val href1 = (contact1.head @@ "xlink:href")

    val contact2 = (md2 \\ "contact" filter { _ @@ "xlink:href" == href1 })

    (contact1.head, contact2.head)
  }
  def nonValidatedContacts = {
    val (contact1, contact2) = loadMetadata
    (contact1 @@ "xlink:role" must_== List(GeocatConstants.HREF_NON_VALIDATED_ROLE_STRING)) and
      (contact2 @@ "xlink:role" must_== List(GeocatConstants.HREF_NON_VALIDATED_ROLE_STRING))
  }

  def validateContact = {
    val contact = loadMetadata._1
    val SharedObjectHrefExtractor(obj) = (contact @@ "xlink:href").head 
    
    ValidateSharedObject(obj.id, obj.objType).execute()
  }

  def isValidatedContact = {
      val (contact1, contact2) = loadMetadata

    (contact1 @@ "xlink:role" filter {_.nonEmpty} must_== List()) and
      (contact2.head @@ "xlink:role" filter {_.nonEmpty} must_== List())
  }
  
  
  def createAndDelete = {
    val Create = CreateNonValidatedUser(User(uuid,SharedUserProfile))
    val newEmail = "newemail"+uuid+"@cc.cc"
    
    config.adminLogin.execute()
    
    val creation = Create.execute()
    val id = GeocatListUsers.execute(uuid.toString()).value.head.userId
    val update = new UpdateSharedUser(Create.user.copy(idOption = Some(id),email = newEmail),false).execute()
    val updatedUser = update.value.loadUser
    val deleteUser = DeleteSharedUser(id,true).execute()

    (creation.basicValue.responseCode must_== 200) and
    (updatedUser.email must_== newEmail) and  // id does not look up correct id for some reason :(
    (deleteUser.basicValue.responseCode must_== 200)
  }

  def createAndValidateNoInterference = {

    // TODO this is broken because the must exceptions are spread through-out the method
    // I have added a org.specs2.matcher.MustThrownMatchers to make this test work but it should be refactored

    val name = "metadata-validate-formats-spec.xml"

    val (_, importMd) = ImportMetadata.defaults(uuid, "/geocat/data/" + name, false, getClass)

    val originalMetadataValue = (UserLogin then importMd then GetEditingMetadataXml).execute().value
    val (id, originalXml) = (originalMetadataValue.id, originalMetadataValue.xml.right.get)
    
    registerNewMd(Id(id))

    val xlinks = XLink.findAll(originalXml, AddSites.distributionFormat)

    val formatId = xlinks.find { _.formatVersion == "2" }.get.id

    val validationResponse = (config.adminLogin then
      ValidateSharedObject(formatId, SharedObjectTypes.formats) then
      GetEditingMetadataXml.setIn(Id(id))).execute()

    val mdAfterValidation = validationResponse.value.getXml

    val newXlinks = XLink.findAll(mdAfterValidation, AddSites.distributionFormat)

    
    (xlinks must have size (3)) and
        (newXlinks.map { _.id } must haveTheSameElementsAs(xlinks.map { _.id })) and
        (newXlinks.filter(_.isValidated) must have size(1)) and 
        (newXlinks.filterNot(_.isValidated) must have size(2)) and
        (xlinks.filter(_.isValidated) must beEmpty) and 
        (xlinks.filterNot(_.isValidated) must have size(3))
  }
  def createAndValidateKeepRole = {
    val name = "metadata-validate-contact-138548.xml"

    val (_,importMd) = ImportMetadata.defaults(uuid, "/geocat/data/"+name, false, getClass)

    val originalMetadataValue = (UserLogin then importMd then GetEditingMetadataXml).execute().value
    val (id,originalXml) = (originalMetadataValue.id,originalMetadataValue.xml.right.get)
    registerNewMd(Id(id))

    val xlinks = XLink.findAll(originalXml,AddSites.contact)
    xlinks must haveSize (1)

    val contactId = xlinks(0).id
    val afterValidation = (config.adminLogin
      then ValidateSharedObject(contactId,SharedObjectTypes.contacts) then
      GetEditingMetadataXml.setIn(Id(id))).execute()

    afterValidation.value.withXml { xml =>
      val newXlinks = XLink.findAll(xml,AddSites.contact)

      newXlinks must haveSize (1)
      newXlinks(0).url must_== xlinks(0).url
    }
  }
  
}