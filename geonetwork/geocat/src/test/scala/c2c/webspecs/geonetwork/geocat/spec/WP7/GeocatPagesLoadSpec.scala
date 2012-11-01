package c2c.webspecs
package geonetwork
package geocat
package spec.WP7

import java.util.Enumeration
import collection.JavaConverters._
import org.specs2.specification.Step
import c2c.webspecs.GetRequest
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import java.util.zip.ZipFile
import scalax.file.Path
import scalax.file.defaultfs.DefaultPath
import scalax.io.Resource
import java.util.zip.ZipEntry

@RunWith(classOf[JUnitRunner])
class GeocatPagesLoadSpec extends GeocatSpecification {
  def is =
    sequential ^ "This spec verifies that core webpages return 200 response codes" ^ Step(setup) ^
      "Import a metadata" ^ Step(importAndGetIds) ^
      "${eng/geocat} Loads" ! loadPage ^
      "${fre/geocat} Loads" ! loadPage ^
      "${ger/geocat} Loads" ! loadPage ^
      "${eng/admin} Loads" ! loadPage ^
      "Loads metadata.show for one of the metadata" ! loadMetadataShow ^
  Step(tearDown)

  lazy val importAndGetIds = importMd(2, identifier = datestamp)
  def select = {
    // first do a search because that is required for a selection
    val correctSearch = correctResults(2, identifier = datestamp)("")
    GetRequest("metadata.select", "id" -> 0, "selected" -> "add-all").execute()
    val correctSelection = GetRequest("metadata.select", "selected" -> "status").execute().value.getXml.text.trim must_== "2"

    correctSearch and correctSelection
  }

  val loadPage = (spec:String) => {
    val path = extract1(spec)
    
    GetRequest(path).execute() must haveA200ResponseCode
  }
  
  def loadMetadataShow = {
    GetRequest("metadata.show", 'id -> importAndGetIds(1)).execute() must haveA200ResponseCode
  }
}