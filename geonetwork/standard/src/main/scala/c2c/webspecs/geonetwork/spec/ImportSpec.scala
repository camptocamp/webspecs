package c2c.webspecs
package geonetwork
package spec

import ImportStyleSheets._

object ImportSpec extends GeonetworkSpecification {
  "Geocat" should {

    "import a iso19139 metadata" in {
      val name = "metadata.iso19139.xml"

      val ImportMd = ImportMetadata(config.resourceFile("data/"+name),GM03_V1,true)

      val request = (
        UserLogin then
        ImportMd startTrackingThen
        GetMetadataXmlFromResult() trackThen
        DeleteMetadata trackThen
        GetMetadataXmlFromResult())

      val (importResponse, findResponse, deleteResponse, secondFindResponse) = request(None).tuple
        importResponse.basicValue.responseCode must_== 200
        findResponse.basicValue.responseCode must_== 200
        findResponse.value.withXml { md =>
            md \\ "ERROR" must beEmpty
            // TODO better checks
          }

        deleteResponse.basicValue.responseCode must_== 200
        secondFindResponse.value.xml.right.toOption must beNone
    }
  }
}
