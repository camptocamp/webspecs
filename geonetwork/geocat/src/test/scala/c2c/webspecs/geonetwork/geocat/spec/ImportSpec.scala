package c2c.webspecs
package geonetwork
package geocat
package spec

import GeocatImportStyleSheets._
import org.specs2.specification.Step

class ImportSpec extends GeonetworkSpecification {
  def is =
  "This specification imports Geocat specific metadata "    ^ Step(setup) ^
    "import a gm03 V1 metadata"                             ! importGM03 ^
    "import a gm03 V2 metadata"                             ! importGM03V2 ^
    "import a iso19139.che metadata"                        ! importISO19139CHE ^
                                                            Step(tearDown)

  def importGM03 = {
    val name = "metadata.gm03_V1.xml"
    val (_,content) = ImportMetadata.importDataFromClassPath("/data/"+name, getClass)
    val ImportMd = ImportMetadata.findGroupId(content,GM03_V1,true)

    val request = (
      UserLogin then
      ImportMd startTrackingThen
      GetMetadataXml() trackThen
      DeleteMetadata trackThen
      GetMetadataXml())


    val (importResponse, findResponse,deleteResponse,secondFindResponse) = request(None).tuple
    importResponse.basicValue.responseCode must_== 200
    findResponse.basicValue.responseCode must_== 200
    findResponse.value.withXml{ md  =>
        md \\ "ERROR" must beEmpty
        // TODO better checks
      }

    deleteResponse.basicValue.responseCode must_== 200
    secondFindResponse.value.xml.right.toOption must beNone
  }

  def importGM03V2 = {
      val name = "metadata.gm03_V2.xml"

      val (_,content) = ImportMetadata.importDataFromClassPath("/data/"+name, getClass)

      val ImportMd = ImportMetadata.findGroupId(content,GM03_V1,true)

      val request = (
        UserLogin then
        ImportMd startTrackingThen
        GetMetadataXml() trackThen
        DeleteMetadata trackThen
        GetMetadataXml()
      )

      val (importResponse, findResponse,deleteResponse,secondFindResponse) = request(None).tuple

      importResponse.basicValue.responseCode must_== 200
      findResponse.basicValue.responseCode must_== 200
      findResponse.value.withXml { md =>
          md \\ "ERROR" must beEmpty
          // TODO better checks
        }

      deleteResponse.basicValue.responseCode must_== 200
      secondFindResponse.value.xml.right.toOption must beNone
    }

    def importISO19139CHE = {
      val name = "metadata.iso19139.che.xml"
      val (_,content) = ImportMetadata.importDataFromClassPath("/data/"+name, getClass)

      val ImportMd = ImportMetadata.findGroupId(content,GM03_V1,true)

      val request = (
        UserLogin then
        ImportMd startTrackingThen
        GetMetadataXml() trackThen
        DeleteMetadata trackThen
        GetMetadataXml())

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
