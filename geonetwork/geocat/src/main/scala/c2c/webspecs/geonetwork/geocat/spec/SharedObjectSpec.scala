package c2c.webspecs
package geonetwork
package geocat
package spec

import xml.{NodeSeq, XML}

object SharedObjectSpec extends GeonetworkSpecification(UserProfiles.UserAdmin) {
  "Geocat" should {
    "Allow creation and deletion of shared users" in {
      val Create = CreateUser(User())

      (UserLogin then Create)(None).basicValue.responseCode must be_>= (400)

      val newEmail = "newemail@cc.cc"

      val request:AccumulatingRequest2[Any,UserValue,UserValue,IdValue] = (config.adminLogin then
        Create startTrackingThen
        UpdateUser(User(email = newEmail)) trackThen        // TODO add shared profile
        DeleteUser())

      val (creation, update, deleteUser) = request(None).tuple
      creation.basicValue.responseCode must_== 200
      creation.value.user.idOption must beSome

      update.value.user.idOption must beSome
      update.value.loadUser.email must_== newEmail  // id does not look up correct id for some reason :(

      deleteUser.basicValue.responseCode must_== 200
    }

    "Allow creation of shared format and validate it without interfering with others in metadata" in {
      val testDataFileName = "metadata-validate-formats-spec.xml"

      val ImportTestData = ImportMetadata(config.resourceFile("data/"+testDataFileName),ImportStyleSheets.NONE,false)

      val originalMetadataValue = (UserLogin then ImportTestData then GetMetadataXml())(None).value
      val (id,originalXml) = (originalMetadataValue.id,originalMetadataValue.xml.right.get)

      val xlinks = XLink.findAll(originalXml,AddSites.distributionFormat)
      xlinks must haveSize (3)

      val formatId = xlinks.find{_.formatVersion == "2"}.get.id

      val afterValidation = (config.adminLogin then
        ValidateSharedObject(formatId,SharedObjectTypes.formats) then
        GetMetadataXml(OutputSchemas.CheRecord).setIn(Id(id)))(None)

      afterValidation.value.withXml { xml =>
        val newXlinks = XLink.findAll(xml,AddSites.distributionFormat)

        newXlinks.map{_.id} must haveSameElementsAs (xlinks.map{_.id})

        val validated = newXlinks collect {
          case xlink if xlink.isValidated => xlink
        }
        validated must haveSize (2)
        val invalidated = newXlinks collect {
          case xlink if xlink.nonValidated => xlink
        }
        invalidated must haveSize (1)
      }
    }
    "Allow creation of shared format and validate it without losing role information" in {
      val testDataFileName = "metadata-validate-contact-138548.xml"

      val ImportTestData = ImportMetadata(config.resourceFile("data/"+testDataFileName),ImportStyleSheets.NONE,false);

      val originalMetadataValue = (UserLogin then ImportTestData then GetMetadataXml())(None).value
      val (id,originalXml) = (originalMetadataValue.id,originalMetadataValue.xml.right.get)

      val xlinks = XLink.findAll(originalXml,AddSites.contact)
      xlinks must haveSize (1)

      val contactId = xlinks(0).id
      val afterValidation = (config.adminLogin
        then ValidateSharedObject(contactId,SharedObjectTypes.contacts) then
        GetMetadataXml(OutputSchemas.CheRecord).setIn(Id(id)))(None)

      afterValidation.value.withXml { xml =>
        val newXlinks = XLink.findAll(xml,AddSites.contact)

        newXlinks must haveSize (1)
        newXlinks(0).url must_== xlinks(0).url
      }
    }
  }
}
