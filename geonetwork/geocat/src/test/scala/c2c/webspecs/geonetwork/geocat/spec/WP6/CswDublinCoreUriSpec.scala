package c2c.webspecs
package geonetwork
package geocat
package spec.WP6

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.csw._
import java.util.UUID

@RunWith(classOf[JUnitRunner])
class CswDublinCoreUriSpec extends GeocatSpecification { def is =
   "This specification verifies that URI links have a link body" ^ Step(setup) ^
     "Import a test metadata" ^ Step(importTestData) ^
       "Perform search in dublin core and verify URI elements have uri" ! checkLinks ^ Step(tearDown)

  val childUUID = UUID.randomUUID().toString
  val parentUUID = UUID.randomUUID().toString
     
  def importTestData = {
    val childReplacements = Map("{uuid}" -> childUUID)
    val parentReplacements = Map("{uuid}" -> parentUUID, "{child-uuid}" -> childUUID)
    val childImportRequest = ImportMetadata.defaultsWithReplacements(childReplacements,"/geocat/data/MDD-with-parent.xml", false, getClass, ImportStyleSheets.NONE)._2.copy(uuidAction = UuidAction.overwrite)
    val parentImportRequest = ImportMetadata.defaultsWithReplacements(parentReplacements,"/geocat/data/MDS-with-MDD-linked.xml", false, getClass, ImportStyleSheets.NONE)._2.copy(uuidAction = UuidAction.overwrite)
    
    val childResponse = childImportRequest.execute()
    registerNewMd(Id(childResponse.value.id))
    
    val parentResponse = parentImportRequest.execute()
    registerNewMd(Id(parentResponse.value.id))
    
    (childResponse must haveA200ResponseCode) and
        (parentResponse must haveA200ResponseCode)
  } 
     
  def checkLinks = {
    val response = CswGetRecordsRequest(
        PropertyIsEqualTo("_uuid",childUUID).xml, 
        resultType=ResultTypes.results, 
        outputSchema = OutputSchemas.Record).execute()
    
    val uriElems = response.value.getXml \\ "URI"
    val emptyLinks = uriElems filter (_.text.trim.isEmpty)
    
    (uriElems must not beEmpty) and (emptyLinks must beEmpty)
  }
}