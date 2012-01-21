package c2c.webspecs
package geonetwork
package geocat
package spec

import GeocatImportStyleSheets._
import ImportStyleSheets._

import org.specs2.specification.Step

class ImportSpec extends GeocatSpecification {
  def is =
  "This specification imports Geocat specific metadata "    ^ Step(setup) ^
    "import a ${gm03 V1} metadata"                             ! doImport ^
    "import a ${gm03 V2} metadata"                             ! doImport ^
    "import a ${iso19139.che} metadata"                        ! doImport ^
                                                            Step(tearDown)

  val doImport = (s:String) => {
    val importMetadata = extract1(s) match {
      case "gm03 V1" =>
        ImportMetadata.defaults(uuid, "/geocat/data/metadata.gm03_V1.xml", true, getClass, GM03_V1)._2
      case "gm03 V2" =>
        ImportMetadata.defaults(uuid, "/geocat/data/metadata.gm03_V2.xml", true, getClass, GM03_V2)._2
      case "iso19139.che" =>
        ImportMetadata.defaults(uuid, "/geocat/data/metadata.iso19139.che.xml", true, getClass, NONE)._2

    }

    val importResponse = (UserLogin then importMetadata).execute()

    val id = importResponse.value
    val findResponse = GetMetadataXml().execute(id)
    val deleteResponse = DeleteMetadata.execute(id)
    val secondFindResponse = GetMetadataXml().execute(id)

    (importResponse must haveA200ResponseCode) and
      (findResponse must haveA200ResponseCode) and
      (findResponse.value.getXml \\ "ERROR" must beEmpty) and
      (deleteResponse must haveA200ResponseCode) and
      (secondFindResponse.value.xml.right.toOption must beNone)
  }

}
