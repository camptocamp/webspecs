package c2c.webspecs
package geonetwork
package geocat
package spec.WP5

import csw._
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.ImportStyleSheets.NONE
import c2c.webspecs.geonetwork._
import c2c.webspecs.{ XmlValue, Response, IdValue, GetRequest }
import accumulating._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import scala.xml.transform.BasicTransformer
import c2c.webspecs.geonetwork.geocat.spec.WP3.ProcessImportedMetadataSpec
import scala.xml.Node
import scala.xml.XML
import scala.xml.Elem
import org.specs2.execute.Result
import java.util.Date
import org.specs2.matcher.ContainMatcher
import org.specs2.control.LazyParameters
import org.specs2.control.LazyParameter
import scala.xml.NodeSeq
import org.specs2.control.LazyParameter

@RunWith(classOf[JUnitRunner])
class CswResetIndexReaderAfterImportSpec extends SearchSpecification { def is =
    sequential ^ "This spec verifies a fix of a bug where imported data cannot be immediately found" ^ Step(setup) ^
    "Perform a search and verify the metadata has not yet been imported"                             ! correctResults(0, identifier=datestamp) ^
    "Import a metadata"                                                                              ^ Step(importMd(1, identifier=datestamp)) ^
    "Assert that the metadata is found"                                                              ! correctResults(1, identifier=datestamp) ^
    "For the second part of the bug logout"                                                          ^ Step(logout) ^
    "perform the search (expecting 0 results)"                                                       ! correctResults(0, identifier=datestamp) ^
    "Then log back in"                                                                               ^ Step(login) ^
    "perform search and expect to find record again (there used to be caching issues"                ! correctResults(1, identifier=datestamp) ^
                                                                                                       Step(tearDown)
                                                                   
  def logout = GeonetworkLogout.execute()
  def login = UserLogin.execute()
}