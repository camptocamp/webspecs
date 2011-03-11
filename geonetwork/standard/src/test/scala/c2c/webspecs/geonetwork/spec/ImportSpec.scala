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
        ImportMd trackThen
        GetMetadataXmlFromResult() trackThen
        DeleteMetadata trackThen
        GetMetadataXmlFromResult())

      request(None) match {
        case AccumulatedResponse.IncludeLast(importResponse, findResponse, deleteResponse, secondFindResponse) =>
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
}
