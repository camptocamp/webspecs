package c2c.webspecs
package geonetwork
package spec

object EditSpec extends GeonetworkSpecification {
  "Geocat" should {

    "create a metadata and add a shared extent" in {
      val createMetadata = CreateMetadata(config,config.sampleDataTemplateIds(0))
      val addNewExtent = AddNewExtent()
      val request = (
        config.login then
        createMetadata then
        GetMetadataXmlFromResult() startTrackingThen
        StartEditing() then
        addNewExtent then
        GetMetadataXmlFromResult() trackThen
        DeleteMetadata)


        val (originalMd, finalMetadata, _) = request(None).tuple
        val originalXmlValue = originalMd.value
        val finalMetadataValue = finalMetadata.value
        val originalExtents = originalXmlValue.withXml { _ \\ "extent"}
        val finalExtents = finalMetadataValue.withXml {_ \\ "extent"}

        finalExtents.size must beGreaterThan (0)
        (originalExtents.size + 1) must beEqualTo (finalExtents.size)

        val addedExtent = finalExtents filterNot {originalExtents contains _}

/*          val identifier = addedExtent \\ "MD_Identifier" \\ "CharacterString"
        identifier must haveSize (1)
        identifier.text.trim must beEqualTo (addNewExtent.extentId)

        (addedExtent \\ "EX_BoundingPolygon") must haveSize (1)
        (addedExtent \\ "EX_GeographicBoundingBox") must haveSize (1)
*/
    }

    "create a metadata and add a new contact" in {
      val Create = CreateMetadata(config,config.sampleDataTemplateIds(0))
      val request = (
        config.login then
        Create then
        AddNewContact() startTrackingThen
        DeleteMetadata)

        request(None)._1.value.href must notBeEmpty
    }
  }
}
