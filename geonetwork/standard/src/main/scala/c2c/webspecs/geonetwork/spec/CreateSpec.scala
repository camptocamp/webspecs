package c2c.webspecs
package geonetwork
package spec

import org.specs2.specification.Step
import org.mockito.internal.matchers.And


class CreateSpec extends GeonetworkSpecification { def spec =

  "This specification test creating metadata"                     ^
    "create a new metadata document from a template"              ! fromTemplate



  def fromTemplate = {
      val createMd = CreateMetadata(config,config.sampleDataTemplateIds(0))

      val request = (
          config.login then
          createMd startTrackingThen
          GetEditingMetadata trackThen
          DeleteMetadata trackThen
          GetEditingMetadata)

      val (createResponse, findResponse,deleteResponse,secondFindResponse) = request(None).tuple

      (createResponse.basicValue.responseCode must_== 200) and
      (findResponse.basicValue.responseCode must_== 200) and
      (findResponse.value.withXml{md =>
          md \\ "ERROR" must beEmpty
          // TODO better checks
      }) and
      (deleteResponse.basicValue.responseCode must_== 200) and
      (secondFindResponse.value.xml.right.toOption must beNone)
    }
      /*

    "create a service metadata from a template" in {
      val createMd = CreateMetadata(config, config.sampleServiceTemplateIds(0))

      val request = (
          config.login then
          createMd startTrackingThen
          GetEditingMetadata trackThen
          DeleteMetadata trackThen
          GetEditingMetadata)

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
  }*/
}
