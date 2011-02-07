package org.fao.geonet
package stress

import StandardSharedExtents.KantonBern
import actors.Futures

object ConcurrentEditSpec extends StressSpecification(10) {
  "Geocat" should {

    "be able to handle multiple users editing and creating metadata" in {

      val create = CreateMetadata(constants,constants.sampleDataTemplateIds(0))
      run(UserLogin  then create then GetMetadataXml() then StartEditing() then
              AddExtentXLink(KantonBern,true) then AddNewContact() then DeleteMetadata())
    }
  }
}
