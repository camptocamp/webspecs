package c2c.webspecs
package geonetwork
package spec

import AccumulatedResponse._

object CreateSpec extends GeonetworkSpecification {
  "Geocat" should {
    "create a new metadata document from a template" in {
      val createMd = CreateMetadata(config,config.sampleDataTemplateIds(0))

      val request = (
          config.login then
          createMd trackThen
          GetEditingMetadataFromResult() trackThen
          DeleteMetadata trackThen
          GetEditingMetadataFromResult())
      val result = request(None)

      result match {
        case IncludeLast(createResponse, findResponse,deleteResponse,secondFindResponse) =>
          createResponse.basicValue.responseCode must_== 200
          findResponse.basicValue.responseCode must_== 200
          findResponse.value.asInstanceOf[XmlValue].withXml{md =>
              md \\ "ERROR" must beEmpty
              // TODO better checks
            }
          deleteResponse.basicValue.responseCode must_== 200
          secondFindResponse.value.asInstanceOf[XmlValue].xml.right.toOption must beNone
      }
    }


    "create a service metadata from a template" in {
      val createMd = CreateMetadata(config, config.sampleServiceTemplateIds(0))

      val request = (
          config.login then
          createMd trackThen
          GetEditingMetadataFromResult() trackThen
          DeleteMetadata trackThen
          GetEditingMetadataFromResult())

      val response = request(None)
      response match {
        case IncludeLast(createResponse, findResponse,deleteResponse,secondFindResponse) =>
          createResponse.basicValue.responseCode must_== 200
          findResponse.basicValue.responseCode must_== 200
          findResponse.value.asInstanceOf[XmlValue].withXml{md =>
              md \\ "ERROR" must beEmpty
              // TODO better checks
            }
          deleteResponse.basicValue.responseCode must_== 200
          secondFindResponse.value.asInstanceOf[XmlValue].xml.right.toOption must beNone
      }
    }
  }
}
