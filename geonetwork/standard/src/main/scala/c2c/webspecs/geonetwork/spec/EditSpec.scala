package c2c.webspecs
package geonetwork
package spec

import org.specs2.specification.Step

class EditSpec extends GeonetworkSpecification {def is =

  "This specification tests editing metadata"                     ^ Step(setup) ^
    "create a metadata and add an extent"                         ! addExtent ^
    "create a metadata and add a new contact"                     ! addContact ^
                                                                  Step(tearDown)

    def addExtent = {
      val createMetadata = CreateMetadata(config,config.sampleDataTemplateIds(0))
      val addNewExtent = AddNewExtent()
      val request = (
        config.login then
        createMetadata then
        GetMetadataXml() startTrackingThen
        StartEditing() then
        addNewExtent then
        GetMetadataXml() trackThen
        DeleteMetadata)

        val (originalMd, finalMetadata, _) = request(None).tuple
        val originalXmlValue = originalMd.value
        val finalMetadataValue = finalMetadata.value
        val originalExtents = originalXmlValue.withXml { _ \\ "extent"}
        val finalExtents = finalMetadataValue.withXml {_ \\ "extent"}

        (finalExtents.size must beGreaterThan (0)) and
        ((originalExtents.size + 1) must beEqualTo (finalExtents.size))

/*
        val addedExtent = finalExtents filterNot {originalExtents contains _}

        val identifier = addedExtent \\ "MD_Identifier" \\ "CharacterString"
        identifier must haveSize (1)
        identifier.text.trim must beEqualTo (addNewExtent.extentId)

        (addedExtent \\ "EX_BoundingPolygon") must haveSize (1)
        (addedExtent \\ "EX_GeographicBoundingBox") must haveSize (1)
*/
    }

    def addContact = {
      val Create = CreateMetadata(config,config.sampleDataTemplateIds(0))
      val request = (
        config.login then
        Create then
        AddNewContact() startTrackingThen
        DeleteMetadata)

        request(None)._1.value.href must not be empty
    }
}
