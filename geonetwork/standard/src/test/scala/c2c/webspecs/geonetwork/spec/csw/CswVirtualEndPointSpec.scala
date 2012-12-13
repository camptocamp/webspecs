package c2c.webspecs
package geonetwork
package spec.csw

import csw._
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
class CswVirtualEndPointSpec extends GeonetworkSpecification { def is = 
  "This specification test a virtual endpoint" ^ step(setup) ^ step(importData) ^
    "GetRecords should only return the metadata with spatialRepresentationType vector" ! getRecords ^
    "GetRecordById should return a record if it has correct spatialRepresentationType" ! canGetById ^
    "GetRecordById should not return a record if it does not have a correct spatialRepresentationType" ! canNotGetById ^
                                                            step(tearDown)

  lazy val importData = {
    val replace1 = Map("{uuid}" -> uuid.toString.toUpperCase, "{spatialRepType}" -> "vector")
    val importRequest1 = ImportMetadata.defaultsWithReplacements(replace1,"/geonetwork/data/bare-iso19139.xml",false,getClass)._2.copy(uuidAction = UuidAction.overwrite)
    val replace2 = Map("{uuid}" -> datestamp, "{spatialRepType}" -> "paper")
    val importRequest2 = ImportMetadata.defaultsWithReplacements(replace2,"/geonetwork/data/bare-iso19139.xml",false,getClass)._2.copy(uuidAction = UuidAction.overwrite)
    Thread.sleep(500)
    new{ 
      val mdIdWithVector = importRequest1.execute().value
      val mdIdWithOutVector = importRequest2.execute().value
    }
  }

  def getRecords = {
    val response = CswGetRecordsRequest(Nil, url="csw-integration-test").execute().value.getXml
    (response \\ "@numberOfRecordsMatched").head.text.toInt must_== 1
  } 
  def canGetById = {
    val response = CswGetRecordById(uuid.toString.toUpperCase, url="csw-integration-test").execute().value.getXml
    (response \ "_") must haveSize(1)
  }
def canNotGetById = {
    val response = CswGetRecordById(datestamp, url="csw-integration-test").execute().value.getXml
    (response \ "_").headOption must beNone
  }
}