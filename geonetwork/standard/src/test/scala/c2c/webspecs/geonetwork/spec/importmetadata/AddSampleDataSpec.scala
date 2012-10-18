package c2c.webspecs.geonetwork
package spec.importmetadata

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
    "AddSpec".title         ^ Step(setup) ^ Step{deleteAllMetadata(adminLogin = true);config.adminLogin.execute()} ^
      "Add Sample data"     ^ Step(addSampleData) ^
      "Add Sample data request must have completed correctly"     ! addSampleRequestCompletedCorrectly ^
      "sample data added"   ! dataHasBeenAdded ^
                           Step(deleteAllMetadata(adminLogin = true)) ^ Step (tearDown)

  lazy  val addSampleData = AddAllSampleData.execute()

  def addSampleRequestCompletedCorrectly = {
    (addSampleData must haveA200ResponseCode) and
      ((addSampleData.value.getXml \\ "@status").text.trim must_== "true")

  }
  def dataHasBeenAdded = XmlSearch().range(1,5).execute().value.records.size must be_> (0)
    
}