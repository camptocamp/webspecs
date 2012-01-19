package c2c.webspecs.geonetwork.spec.sampledata

import c2c.webspecs._
import geonetwork._

import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

/**
 * User: jeichar
 * Date: 1/19/12
 * Time: 4:21 PM
 */
@RunWith(classOf[JUnitRunner])
class AddSampleDataSpec extends GeonetworkSpecification {
  def is =
    "AddSpec".title         ^ Step(setup) ^ Step(cleanOutDatabase) ^
      "Add Sample data"     ^ Step(addSampleData) ^
      "sample data added"   ! dataHasBeenAdded ^
                           Step(cleanOutDatabase) ^ Step (tearDown)

  def cleanOutDatabase = {
    (XmlSearch() then SelectAll then MetadataBatchDelete).execute()

    XmlSearch().execute()
  }

  def addSampleData = {
    val addResult = new GetRequest(
      "metadata.samples.add",
      'uuidAction -> 'nothing,
      'file_type -> 'mef,
      'schema -> "csw-record,dublin-core,fgdc-std,iso19110,iso19115,iso19139").execute()
    
    (addResult must haveA200ResponseCode) and
      ((addResult.value.getXml \\ "@status") must_== "true")
  }

  def dataHasBeenAdded = {
    println(XmlSearch().execute().value.getText)
    pending
  } 
    
}