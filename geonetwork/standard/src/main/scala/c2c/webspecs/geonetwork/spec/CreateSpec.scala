package c2c.webspecs
package geonetwork
package spec

import org.mockito.internal.matchers.And
import org.specs2.specification.{Outside, Around, Step}
import accumulating.AccumulatedResponse3


class CreateSpec extends GeonetworkSpecification { def spec =

  "This specification test creating metadata"                     ^
    "create a new metadata document from a template"              ! fromTemplate ^
    "create a service metadata from a service metadata template"  ! serviceMd

  def fromTemplate = {
      val (createResponse, findResponse,deleteResponse,secondFindResponse) =
        createMdRequest(config.sampleDataTemplateIds(0))

      (createResponse.basicValue.responseCode must_== 200) and
      (findResponse.basicValue.responseCode must_== 200) and
      (findResponse.value.withXml{md =>
          md \\ "ERROR" must beEmpty
          // TODO better checks
      }) and
      (deleteResponse.basicValue.responseCode must_== 200) and
      (secondFindResponse.value.xml.right.toOption must beNone)
    }

    def serviceMd = {

      val (createResponse, findResponse,deleteResponse,secondFindResponse) =
        createMdRequest(config.sampleServiceTemplateIds(0))

      (createResponse.basicValue.responseCode must_== 200) and
      (findResponse.basicValue.responseCode must_== 200) and
      (findResponse.value.withXml{md =>
          md \\ "ERROR" must beEmpty
          // TODO better checks
      }) and
      (deleteResponse.basicValue.responseCode must_== 200) and
      (secondFindResponse.value.xml.right.toOption must beNone)
  }

  def createMdRequest(templateId:String) = {
    val createMd = CreateMetadata(config, templateId)

    val request = (
        config.login then
        createMd startTrackingThen
        GetEditingMetadata trackThen
        DeleteMetadata trackThen
        GetEditingMetadata)

    request(None).tuple
  }
}
