package c2c.webspecs
package geonetwork
package spec

import ImportStyleSheets._
import org.specs2.specification.Step

class ImportSpec extends GeonetworkSpecification {def is =

  "This specification tests importing complete metadata files"    ^ Step(setup) ^
    "import a ${iso19139} metadata"                               ! importIso19139 ^
                                                                  Step(tearDown)

  def importIso19139 = {
      val name = "metadata.iso19139.xml"

      val (data,content) = ResourceLoader.loadDataFromClassPath("/data/"+name,classOf[ImportSpec],uuid)
      val ImportMd = ImportMetadata.findGroupId(content,NONE,true)

      val request = (
        UserLogin then
        ImportMd startTrackingThen
        GetMetadataXml() trackThen
        DeleteMetadata trackThen
        GetMetadataXml())

      val (importResponse, findResponse, deleteResponse, secondFindResponse) = request(None).tuple

      (importResponse must haveA200ResponseCode) and
      (findResponse must haveA200ResponseCode) and
      (findResponse.value.withXml { md =>
          md \\ "ERROR" must beEmpty
          // TODO better checks
      }) and
      (deleteResponse must haveA200ResponseCode) and
      (secondFindResponse.value.xml must beLeft)
    }
}
