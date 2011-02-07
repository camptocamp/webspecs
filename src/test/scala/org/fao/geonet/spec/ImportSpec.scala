package org.fao.geonet
package spec

import org.specs._
import ImportStyleSheets._

object ImportSpec extends GeonetworkSpecification {
  "Geocat" should {

    "import a gm03 V1 metadata" in {
      val name = "metadata.gm03_V1.xml"
      val importMd = ImportMetadata(name,Config.inputStream("data/"+name),GM03_V1,true)

      (config.login then importMd trackThen GetMetadataXml() trackThen DeleteMetadata() trackThen GetMetadataXml()) {
        case AccumulatedResponse(importResponse, findResponse,deleteResponse,secondFindResponse) =>
          importResponse.responseCode must_== 200
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
    "import a gm03 V2 metadata" in {
      val name = "metadata.gm03_V2.xml"
      val importMd = ImportMetadata(name,Config.inputStream("data/"+name),GM03_V2,true)

      (config.login then importMd trackThen GetMetadataXml() trackThen DeleteMetadata() trackThen GetMetadataXml()) {
        case AccumulatedResponse(importResponse, findResponse,deleteResponse,secondFindResponse) =>
          importResponse.responseCode must_== 200
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

    "import a iso19139.che metadata" in {
      val name = "metadata.iso19139.che.xml"
      val importMd = ImportMetadata(name,Config.inputStream("data/"+name),NONE,true)

      (config.login then importMd trackThen GetMetadataXml() trackThen DeleteMetadata() trackThen GetMetadataXml()) {
        case AccumulatedResponse(importResponse, findResponse,deleteResponse,secondFindResponse) =>
          importResponse.responseCode must_== 200
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
