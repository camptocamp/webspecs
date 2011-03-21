package c2c.webspecs
package geonetwork
package spec


object CreateSpec extends GeonetworkSpecification {
  "Geocat" should {
    "create a new metadata document from a template" in {
      val createMd = CreateMetadata(config,config.sampleDataTemplateIds(0))

      val request = (
          config.login then
          createMd startTrackingThen
          GetEditingMetadataFromResult() trackThen
          DeleteMetadata trackThen
          GetEditingMetadataFromResult())

      val (createResponse, findResponse,deleteResponse,secondFindResponse) = request(None).tuple

      createResponse.basicValue.responseCode must_== 200
      findResponse.basicValue.responseCode must_== 200
      findResponse.value.withXml{md =>
          md \\ "ERROR" must beEmpty
          // TODO better checks
        }
      deleteResponse.basicValue.responseCode must_== 200
      secondFindResponse.value.xml.right.toOption must beNone
    }


    "create a service metadata from a template" in {
      val createMd = CreateMetadata(config, config.sampleServiceTemplateIds(0))

      val request = (
          config.login then
          createMd startTrackingThen
          GetEditingMetadataFromResult() trackThen
          DeleteMetadata trackThen
          GetEditingMetadataFromResult())

      val (createResponse, findResponse,deleteResponse,secondFindResponse) = request(None).tuple

      createResponse.basicValue.responseCode must_== 200
      findResponse.basicValue.responseCode must_== 200
      findResponse.value.withXml{md =>
          md \\ "ERROR" must beEmpty
          // TODO better checks
        }
      deleteResponse.basicValue.responseCode must_== 200
      secondFindResponse.value.xml.right.toOption must beNone
    }
  }
}
