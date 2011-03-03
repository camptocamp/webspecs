package c2c.webspecs
package geonetwork
package geocat
package spec

import AccumulatedResponse._
import StandardSharedExtents.KantonBern

object EditSpecWithSharedObjects extends GeonetworkSpecification {
  "Geocat" should {

    "create a metadata and add a shared extent" in {
      val create = CreateMetadata(config,config.sampleDataTemplateIds(0))
      val request = (
        config.login then
        create then
        GetMetadataXml() trackThen
        StartEditing() then
        AddExtentXLink(KantonBern,true) then
        GetMetadataXml() trackThen
        DeleteMetadata())

      request(None) match {
        case r @ Tracked(originalMd, finalMetadata) =>
          val originalExtents = withXml(originalMd) { _ \\ "extent"}
          val finalExtents = withXml(finalMetadata) {_ \\ "extent"}

          finalExtents.size must beGreaterThan (0)
          (originalExtents.size + 1) must beEqualTo (finalExtents.size)

          val addedExtent = finalExtents filterNot {originalExtents contains _}

          val identifier = addedExtent \\ "MD_Identifier" \\ "CharacterString"
          identifier must haveSize (1)
          identifier.text.trim must beEqualTo ("BE")

          (addedExtent \\ "EX_BoundingPolygon") must haveSize (1)
          (addedExtent \\ "EX_GeographicBoundingBox") must haveSize (1)
      }
    }

    "create a metadata and add a new contact" in {
      val Create = CreateMetadata(config,config.sampleDataTemplateIds(0))
      val request = (
        config.login then
        Create then
        AddNewContact() trackThen
        DeleteMetadata())

      request(None) match {
        case r @ Tracked (add) =>
          add.href must notBeEmpty
      }
    }
  }
}
