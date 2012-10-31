package c2c.webspecs
package geonetwork
package spec.csw.search

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import c2c.webspecs.geonetwork.csw._
import c2c.webspecs.login.LogoutRequest

@RunWith(classOf[JUnitRunner])
class CswResetIndexReaderAfterImportSpec extends GeonetworkSpecification with SearchSpecification {
  override def correctResults(numberOfRecords:Int, identifier:String) = (s:String) => {
    val xml = CswGetRecordsRequest(PropertyIsEqualTo("AnyText","Title"+identifier).xml).execute().value.getXml

    (xml \\ "@numberOfRecordsMatched").text.toInt must_== numberOfRecords
  }

  
  def is =
    sequential ^ "This spec verifies a fix of a bug where imported data cannot be immediately found" ^ Step(setup) ^
    "Perform a search and verify the metadata has not yet been imported"                             ! correctResults(0, identifier=datestamp) ^
    "Import a metadata"                                                                              ^ Step(importExtraMd(1, identifier=datestamp)) ^
    "Assert that the metadata is found"                                                              ! correctResults(1, identifier=datestamp) ^
    "For the second part of the bug logout"                                                          ^ Step(logout) ^
    "perform the search (expecting 0 results)"                                                       ! correctResults(0, identifier=datestamp) ^
    "Then log back in"                                                                               ^ Step(login) ^
    "perform search and expect to find record again (there used to be caching issues"                ! correctResults(1, identifier=datestamp) ^
                                                                                                       Step(tearDown)
                                                                   
  def logout = LogoutRequest().execute()
  def login = UserLogin.execute()
}