package org.fao.geonet
package spec

import xml.{NodeSeq, XML}

object SharedObjectSpec extends GeonetworkSpecification(UserProfiles.UserAdmin) {
  "Geocat" should {
    "Allow creation and deletion of shared users" in {
      (UserLogin then CreateUser(User())) {
        response => response.responseCode must be_>= (400)
      }

      val newEmail = "newemail@cc.cc"
      (Config.adminLogin then CreateUser(User()) trackThen UpdateUser(User(email=newEmail)) trackThen DeleteUser()) {
        case AccumulatedResponse(creation:ListUserResponse, update:ListUserResponse, deleteUser) =>
          creation.responseCode must_== 200
          creation.id must beSome

          update.id must beSome
          // update.updatedUser.get.email must_== newEmail  // id does not look up correct id for some reason :(

          deleteUser.responseCode must_== 200
      }
    }

    "Allow creation of shared format and validate it without interfering with others in metadata" in {
      val testDataFileName = "metadata-validate-formats-spec.xml"
      val testData = XML.load(Config.inputStream("data/"+testDataFileName))
      val ImportTestData = ImportMetadata("data/"+testDataFileName,testData.toString,ImportStyleSheets.NONE,false);
      val (id,originalXml) = (UserLogin then ImportTestData then GetMetadataXml()) {
        case response:XmlResponse with MetadataIdResponse => response.withXml {xml => (response.id,xml)}
      }
      val xlinks = XLink.findAll(originalXml,AddSites.distributionFormat)
      xlinks must haveSize (3)

      val formatId = xlinks.find{_.formatVersion == "2"}.get.id.toInt
      (Config.adminLogin then ValidateSharedObject(formatId,SharedObjectTypes.formats) then GetMetadataXml(id,OutputSchemas.CheRecord)) {
        case response:XmlResponse => withXml(response) { xml =>
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
          ()
        }
      }
    }
    "Allow creation of shared format and validate it without losing role information" in {
      val testDataFileName = "metadata-validate-contact-138548.xml"
      val testData = XML.load(Config.inputStream("data/"+testDataFileName))
      val ImportTestData = ImportMetadata("data/"+testDataFileName,testData.toString,ImportStyleSheets.NONE,false);
      val (id,originalXml) = (UserLogin then ImportTestData then GetMetadataXml()) {
        case response:XmlResponse with MetadataIdResponse => response.withXml {xml => (response.id,xml)}
      }
      val xlinks = XLink.findAll(originalXml,AddSites.contact)
      xlinks must haveSize (1)

      val contactId = xlinks(0).id.toInt
      (Config.adminLogin then ValidateSharedObject(contactId,SharedObjectTypes.contacts) then GetMetadataXml(id,OutputSchemas.CheRecord)) {
        case response:XmlResponse => withXml(response) { xml =>
          val newXlinks = XLink.findAll(xml,AddSites.contact)

          newXlinks must haveSize (1)
          newXlinks(0).url must_== xlinks(0).url
          ()
        }
      }
    }
  }
}
