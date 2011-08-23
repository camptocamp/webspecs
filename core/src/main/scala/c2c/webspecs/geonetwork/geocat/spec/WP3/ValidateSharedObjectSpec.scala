package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import c2c.webspecs.geonetwork.geocat.GeocatSpecification
import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import c2c.webspecs.geonetwork.edit.AddSites
import org.specs2.matcher.MustThrownMatchers

@RunWith(classOf[JUnitRunner]) 
class ValidateSharedObjectSpec extends GeocatSpecification with MustThrownMatchers { def is = 
  "Validate Shared Object spec".title 				^ Step(setup) ^
  "This specification tests validation of shared objects"													^
  "This section tests validating a shared object that is present in multiple Metadata"						^
    "First we need to Import the same metadata object twice"												^ Step(ImportTwoMetadata)  ^
    "At this point the xlink in both metadata should no be validated"										! nonValidatedContacts  ^
    "Next validate the imported shared object, in this case a contact"										^ Step(validateContact) ^
    "Once done the xlink in both metadata should be updated to be validated"								! isValidatedContact		^
    																										  endp ^
  "This section tests a couple corner cases of shared object validation"                                    ^ 
    "Allow creation and deletion of shared contacts"                                                        ! createAndDelete ^
    "Allow creation of shared format and validate it without interfering with others in metadata" 			! createAndValidateNoInterference ^
    "Allow creation of shared format and validate it without losing role information"             			! createAndValidateKeepRole ^
    																										  Step(tearDown)

  lazy val ImportTwoMetadata: (IdValue, IdValue) = {
    val importRequest = ImportMetadata.defaults(uuid, "/geocat/data/metadata.iso19139.che.xml", true, classOf[ValidateSharedObject], ImportStyleSheets.NONE)._2
    config.adminLogin()
    val id1 = importRequest().value
    val id2 = importRequest().value
    registerNewMd(id1, id2)
    (id1, id2)
  }

  def loadMetadata = {
    val md1 = GetEditingMetadataXml(ImportTwoMetadata._1).value.getXml
    val md2 = GetEditingMetadataXml(ImportTwoMetadata._2).value.getXml

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
    
    ValidateSharedObject(obj.id, obj.objType)()
  }

  def isValidatedContact = {
    val (contact1, contact2) = loadMetadata

    (contact1 @@ "xlink:role" must_== List("")) and
      (contact2.head @@ "xlink:role" must_== List(""))
  }
  
  
  def createAndDelete = {
    val Create = CreateUser(User(uuid,SharedUserProfile))
    val newEmail = "newemail"+uuid+"@cc.cc"
    
    config.adminLogin()
    
    val creation = Create()
    val id = GeocatListUsers(uuid.toString()).value.head.userId
    val update = UpdateUser(Create.user.copy(idOption = Some(id),email = newEmail))()
    val updatedUser = update.value.loadUser
    val deleteUser = DeleteUser(id)()

    (creation.basicValue.responseCode must_== 200) and
    (updatedUser.email must_== newEmail) and  // id does not look up correct id for some reason :(
    (deleteUser.basicValue.responseCode must_== 200)
  }

  def createAndValidateNoInterference = {

    // TODO this is broken because the must exceptions are spread through-out the method
    // I have added a org.specs2.matcher.MustThrownMatchers to make this test work but it should be refactored

    val name = "metadata-validate-formats-spec.xml"

    val (_, importMd) = ImportMetadata.defaults(uuid, "/geocat/data/" + name, false, getClass)

    val originalMetadataValue = (UserLogin then importMd then GetEditingMetadataXml)(None).value
    val (id, originalXml) = (originalMetadataValue.id, originalMetadataValue.xml.right.get)
    
    registerNewMd(Id(id))

    val xlinks = XLink.findAll(originalXml, AddSites.distributionFormat)

    xlinks must have size (3)

    val formatId = xlinks.find { _.formatVersion == "2" }.get.id

    val validationResponse = (config.adminLogin then
      ValidateSharedObject(formatId, SharedObjectTypes.formats) then
      GetEditingMetadataXml.setIn(Id(id)))(None)

    val mdAfterValidation = validationResponse.value.getXml

    val newXlinks = XLink.findAll(mdAfterValidation, AddSites.distributionFormat)

    newXlinks.map { _.id } must haveTheSameElementsAs(xlinks.map { _.id })

    def validated(xlinks:Seq[XLink]) = xlinks collect {
      case xlink if xlink.isValidated => xlink.id
    }
    validated(newXlinks) must have size(validated(xlinks).size)
    def invalidated(xlinks:Seq[XLink]) = xlinks collect {
      case xlink if xlink.nonValidated => xlink.id
    }
    invalidated(newXlinks) must have size(invalidated(xlinks).size)
  }
  def createAndValidateKeepRole = {
    val name = "metadata-validate-contact-138548.xml"

    val (_,importMd) = ImportMetadata.defaults(uuid, "/geocat/data/"+name, false, getClass)

    val originalMetadataValue = (UserLogin then importMd then GetEditingMetadataXml)(None).value
    val (id,originalXml) = (originalMetadataValue.id,originalMetadataValue.xml.right.get)
    registerNewMd(Id(id))

    val xlinks = XLink.findAll(originalXml,AddSites.contact)
    xlinks must haveSize (1)

    val contactId = xlinks(0).id
    val afterValidation = (config.adminLogin
      then ValidateSharedObject(contactId,SharedObjectTypes.contacts) then
      GetEditingMetadataXml.setIn(Id(id)))(None)

    afterValidation.value.withXml { xml =>
      val newXlinks = XLink.findAll(xml,AddSites.contact)

      newXlinks must haveSize (1)
      newXlinks(0).url must_== xlinks(0).url
    }
  }
  
}