package c2c.webspecs
package geonetwork
package spec.csw

import org.specs2.mutable._
import csw.CswGetRecordById
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

/**
 * Specification for GetRecordsById, also an example of how to write a mutable specification
 *
 * User: jeichar
 * Date: 1/19/12
 * Time: 8:11 AM
 */
@RunWith(classOf[JUnitRunner])
class CswGetRecordsByIdSpec extends GeonetworkSpecification with Specification {
  step(setup)

  "GetRecordsById" should {
    "retrieve a metadata its uuid" in {
      val id = importMd(1,"/geonetwork/data/valid-metadata.iso19139.xml",uuid.toString).head
      val fileId = (GetRawMetadataXml.execute(id).value.getXml \\ "fileIdentifier").text.trim
      val result = CswGetRecordById(fileId).execute()
      (result must haveA200ResponseCode) and
        (result.value.getXml.toString must not beEmpty) and
        ((result.value.getXml \\ "abstract" text).trim must_== "Abstract "+uuid)
    }
  }

  step(tearDown)
}