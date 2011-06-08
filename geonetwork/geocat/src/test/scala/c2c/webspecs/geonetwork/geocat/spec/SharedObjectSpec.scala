package c2c.webspecs
package geonetwork
package geocat
package spec

import org.specs2.specification.Step
import org.specs2.matcher.MustThrownMatchers

class SharedObjectSpec extends GeonetworkSpecification(UserProfiles.UserAdmin) with MustThrownMatchers { def is =
  "This specification tests managing Shared objects"                                              ^ Step(setup) ^
    "Allow creation and deletion of shared users"                                                 ! createAndDelete ^
    "Allow creation of shared format and validate it without interfering with others in metadata" ! createAndValidateNoInterference ^
    "Allow creation of shared format and validate it without losing role information"             ! createAndValidateKeepRole ^
                                                                                                  Step(tearDown)

  def createAndDelete = {
    val Create = CreateUser(User())

    (UserLogin then Create)(None).basicValue.responseCode must be_>= (400)

    val newEmail = "newemail@cc.cc"

    val request = (config.adminLogin then
      Create startTrackingThen
      UpdateUser(User(email = newEmail)) trackThen        // TODO add shared profile
      DeleteUser())

    val (creation, update, deleteUser) = request(None).tuple

    (creation.basicValue.responseCode must_== 200) and
    (creation.value.user.idOption must beSome) and
    (update.value.user.idOption must beSome) and
    (update.value.loadUser.email must_== newEmail) and  // id does not look up correct id for some reason :(
    (deleteUser.basicValue.responseCode must_== 200)
  }

  def createAndValidateNoInterference = {

    // TODO this is broken because the must exceptions are spread through-out the method
    // I have added a org.specs2.matcher.MustThrownMatchers to make this test work but it should be refactored

    val testDataFileName = "metadata-validate-formats-spec.xml"

    val (_,content) = ImportMetadata.importDataFromClassPath("data/"+testDataFileName, getClass)

    val ImportTestData = ImportMetadata.findGroupId(content,ImportStyleSheets.NONE,false)

    val originalMetadataValue = (UserLogin then ImportTestData then GetMetadataXml())(None).value
    val (id,originalXml) = (originalMetadataValue.id,originalMetadataValue.xml.right.get)

    val xlinks = XLink.findAll(originalXml,AddSites.distributionFormat)

    xlinks must have size (3)

    val formatId = xlinks.find{_.formatVersion == "2"}.get.id

    val afterValidation = (config.adminLogin then
      ValidateSharedObject(formatId,SharedObjectTypes.formats) then
      GetMetadataXml(CheRecord).setIn(Id(id)))(None)

    afterValidation.value.withXml { xml =>
      val newXlinks = XLink.findAll(xml,AddSites.distributionFormat)

      newXlinks.map{_.id} must contain (xlinks.map{_.id})

      val validated = newXlinks collect {
        case xlink if xlink.isValidated => xlink
      }
      validated must have size (2)
      val invalidated = newXlinks collect {
        case xlink if xlink.nonValidated => xlink
      }
      invalidated must have size (1)
    }
  }
  def createAndValidateKeepRole = {
    val testDataFileName = "metadata-validate-contact-138548.xml"

    val (_,content) = ImportMetadata.importDataFromClassPath("data/"+testDataFileName, getClass)
    val ImportTestData = ImportMetadata.findGroupId(content,ImportStyleSheets.NONE,false);

    val originalMetadataValue = (UserLogin then ImportTestData then GetMetadataXml())(None).value
    val (id,originalXml) = (originalMetadataValue.id,originalMetadataValue.xml.right.get)

    val xlinks = XLink.findAll(originalXml,AddSites.contact)
    xlinks must haveSize (1)

    val contactId = xlinks(0).id
    val afterValidation = (config.adminLogin
      then ValidateSharedObject(contactId,SharedObjectTypes.contacts) then
      GetMetadataXml(CheRecord).setIn(Id(id)))(None)

    afterValidation.value.withXml { xml =>
      val newXlinks = XLink.findAll(xml,AddSites.contact)

      newXlinks must haveSize (1)
      newXlinks(0).url must_== xlinks(0).url
    }
  }
}
