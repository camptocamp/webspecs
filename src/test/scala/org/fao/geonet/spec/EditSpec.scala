package org.fao.geonet
package spec

import org.specs._
import StandardSharedExtents.KantonBern
import Editing._

object EditSpec extends GeonetworkSpecification {
  "Geocat" should {

    "create a metadata and add a shared extent" in {
      val create = CreateMetadata(constants,constants.sampleDataTemplateIds(0))
      (config.login then create then GetMetadataXml() trackThen StartEditing() then AddExtentXLink(KantonBern,true) then GetMetadataXml() trackThen DeleteMetadata()) {
        case r @ AccumulatedResponse(originalMd, finalMetadata, _) =>
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
      val Create = CreateMetadata(constants,constants.sampleDataTemplateIds(0))
      (config.login then Create then AddNewContact() trackThen DeleteMetadata()) {
        case r @ AccumulatedResponse(add:AddResponse, _) =>
          add.href must notBeEmpty
      }
    }
  }
}
