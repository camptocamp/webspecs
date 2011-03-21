package c2c.webspecs
package geonetwork
package geocat
package spec

import StandardSharedExtents.KantonBern

object EditSpecWithSharedObjects extends GeonetworkSpecification {
  "Geocat" should {

    "create a metadata and add a shared extent" in {
      val create = CreateMetadata(config,config.sampleDataTemplateIds(0))
      val request = (
        config.login then
        create then
        GetMetadataXmlFromResult() startTrackingThen
        StartEditing() then
        AddExtentXLink(KantonBern,true) then
        GetMetadataXmlFromResult() trackThen
        DeleteMetadata)

      val (originalMd, finalMetadata,_) = request(None).values

      val originalExtents = originalMd.withXml { _ \\ "extent"}
      val finalExtents = finalMetadata.withXml {_ \\ "extent"}

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
}
