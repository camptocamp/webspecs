package org.fao.geonet
package spec

import org.specs._
import ImportStyleSheets._

object ImportSpec extends GeonetworkSpecification {
  "Geocat" should {

    "import a iso19139.che metadata" in {
      val name = "metadata.iso19139.xml"
      val importMd = ImportMetadata(name,Config.inputStream("data/"+name),NONE,false)

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
