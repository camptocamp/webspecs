package c2c.webspecs
package geonetwork
package geocat
package spec

import ImportStyleSheets._

object ImportSpec extends GeonetworkSpecification {
  "Geocat" should {

    "import a gm03 V1 metadata" in {
      val name = "metadata.gm03_V1.xml"
      val ImportMd = ImportMetadata(config.resourceFile("data/"+name),GM03_V1,true)

      val request = (
        UserLogin then
        ImportMd startTrackingThen
        GetMetadataXmlFromResult() trackThen
        DeleteMetadata trackThen
        GetMetadataXmlFromResult())


      val (importResponse, findResponse,deleteResponse,secondFindResponse) = request(None).tuple
      importResponse.basicValue.responseCode must_== 200
      findResponse.basicValue.responseCode must_== 200
      findResponse.value.asInstanceOf[XmlValue].withXml{ md  =>
          md \\ "ERROR" must beEmpty
          // TODO better checks
        }

      deleteResponse.basicValue.responseCode must_== 200
      secondFindResponse.value.asInstanceOf[XmlValue].xml.right.toOption must beNone
    }

    "import a gm03 V2 metadata" in {
      val name = "metadata.gm03_V2.xml"

      val ImportMd = ImportMetadata(config.resourceFile("data/"+name),GM03_V1,true)

      val request = (
        UserLogin then
        ImportMd startTrackingThen
        GetMetadataXmlFromResult() trackThen
        DeleteMetadata trackThen
        GetMetadataXmlFromResult()
      )

      val (importResponse, findResponse,deleteResponse,secondFindResponse) = request(None).tuple

      importResponse.basicValue.responseCode must_== 200
      findResponse.basicValue.responseCode must_== 200
      findResponse.value.asInstanceOf[XmlValue].withXml { md =>
          md \\ "ERROR" must beEmpty
          // TODO better checks
        }

      deleteResponse.basicValue.responseCode must_== 200
      secondFindResponse.asInstanceOf[XmlValue].xml.right.toOption must beNone
    }

    "import a iso19139.che metadata" in {
      val name = "metadata.iso19139.che.xml"

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
      findResponse.value.asInstanceOf[XmlValue].withXml { md =>
          md \\ "ERROR" must beEmpty
          // TODO better checks
        }

      deleteResponse.basicValue.responseCode must_== 200
      secondFindResponse.asInstanceOf[XmlValue].xml.right.toOption must beNone
    }
  }
}
