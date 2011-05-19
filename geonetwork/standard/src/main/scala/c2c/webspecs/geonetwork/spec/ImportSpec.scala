package c2c.webspecs
package geonetwork
package spec

import ImportStyleSheets._

class ImportSpec extends GeonetworkSpecification {def spec =

  "This specification tests importing complete metadata files"    ^
    "import a iso19139 metadata"                                  ! importIso19139

  def importIso19139 = {
      val name = "metadata.iso19139.xml"

      val ImportMd = ImportMetadata(config.resourceFile("data/"+name),GM03_V1,true)

      val request = (
        UserLogin then
        ImportMd startTrackingThen
        GetMetadataXml() trackThen
        DeleteMetadata trackThen
        GetMetadataXml())

      val (importResponse, findResponse, deleteResponse, secondFindResponse) = request(None).tuple

      (importResponse.basicValue.responseCode must_== 200) and
      (findResponse.basicValue.responseCode must_== 200) and
      (findResponse.value.withXml { md =>
          md \\ "ERROR" must beEmpty
          // TODO better checks
      }) and
      (deleteResponse.basicValue.responseCode must_== 200) and
      (secondFindResponse.value.xml.right.toOption must be none)
    }
}
