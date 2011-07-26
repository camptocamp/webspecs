package c2c.webspecs
package geonetwork
package geocat
package spec.WP3
import edit._
import org.specs2.specification.Step
import org.specs2.matcher.MustThrownMatchers
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner]) 
class SharedObjectXLinkSpec extends GeocatSpecification(UserProfiles.UserAdmin) with MustThrownMatchers { def is =
  "This specification tests managing Shared objects"                                              ^ Step(setup) ^
    "Allow creation and deletion of shared users"                                                 ! createAndDelete ^
    "Allow creation of shared format and validate it without interfering with others in metadata" ! createAndValidateNoInterference ^
    "Allow creation of shared format and validate it without losing role information"             ! createAndValidateKeepRole ^
                                                                                                  Step(tearDown)

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
