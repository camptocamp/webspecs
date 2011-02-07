package org.fao.geonet
package spec

import org.specs._

object CreateSpec extends GeonetworkSpecification {
  "Geocat" should {
    "create a new metadata document from a template" in {
      val createMd = CreateMetadata(constants,constants.sampleDataTemplateIds(0))

      (config.login then createMd trackThen GetMetadataXml() trackThen DeleteMetadata() trackThen GetMetadataXml()) {
        case AccumulatedResponse(createResponse, findResponse,deleteResponse,secondFindResponse) =>
          createResponse.responseCode must_== 200
          findResponse.responseCode must_== 200
          findResponse.xml.fold(
            error => fail("expected xml to be loaded without error: "+error),
            md => {
              md \\ "ERROR" must beEmpty
              // TODO better checks
            })
          deleteResponse.responseCode must_== 200
          secondFindResponse.xml.right.toOption must beNone
      }
    }

    "create a service metadata from a template" in {
      val createMd = CreateMetadata(constants, constants.sampleServiceTemplateIds(0))

      (config.login then createMd trackThen GetMetadataXml() trackThen DeleteMetadata() trackThen GetMetadataXml()) {
        case AccumulatedResponse(createResponse, findResponse,deleteResponse,secondFindResponse) =>
          createResponse.responseCode must_== 200
          findResponse.responseCode must_== 200
          findResponse.xml.fold(
            error => fail("expected xml to be loaded without error: "+error),
            md => {
              md \\ "ERROR" must beEmpty
              // TODO better checks
            })
          deleteResponse.responseCode must_== 200
          secondFindResponse.xml.right.toOption must beNone
      }
    }

  }
}
