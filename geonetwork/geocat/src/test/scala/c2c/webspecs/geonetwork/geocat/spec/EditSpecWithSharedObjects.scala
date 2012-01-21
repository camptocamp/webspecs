package c2c.webspecs
package geonetwork
package geocat
package spec

import edit._
import StandardSharedExtents.KantonBern
import org.specs2.specification.Step

class EditSpecWithSharedObjects extends GeonetworkSpecification { def is =
  "This specification edits a metadata by adding shared objects"    ^ Step(setup) ^
    "create a metadata and add a shared extent"                     ! addExtent ^
                                                                    Step(tearDown)

  def addExtent = {
    UserLogin.execute()
    val id = CreateMetadata(config, config.sampleDataTemplateIds(0)).execute().value
    registerNewMd(id)
    val beforeEditing = GetMetadataXml().execute(id)
    val metadataAfterAddXLink = (StartEditing() then AddExtentXLink(KantonBern, true) then  GetMetadataXml()).execute(id)


    val originalExtents = beforeEditing.value.getXml \\ "extent"
    val finalExtents = metadataAfterAddXLink.value.getXml \\ "extent"

    finalExtents.size must beGreaterThan(0)
    (originalExtents.size + 1) must beEqualTo(finalExtents.size)

    val addedExtent = finalExtents filterNot {
      originalExtents contains _
    }

    val identifier = addedExtent \\ "MD_Identifier" \\ "CharacterString"
    identifier must haveSize(1)
    identifier.text.trim must beEqualTo("BE")

    (addedExtent \\ "EX_BoundingPolygon") must haveSize(1)
    (addedExtent \\ "EX_GeographicBoundingBox") must haveSize(1)
  }
}
