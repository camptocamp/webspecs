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
import scala.xml.XML

@RunWith(classOf[JUnitRunner])
class MefExportNoXLinksSpec extends GeocatSpecification { def is =
    sequential ^ "This spec verifies that xlinks are not exported in MEF files" ^ Step(setup) ^
    "Import a metadata"                                                                              ^ Step(importAndGetIds) ^
    "Select imported MD"                                                                             ! select ^
    "Download MEF package"                                                                           ^ Step(download) ^
    "verify the metadata is in iso19139"                                                             ! che ^
                                                                                                       Step(tearDown)
 
  lazy val importAndGetIds = importMd(2, md="/geocat/data/comprehensive-iso19139che.xml", identifier=datestamp)
  def select = {
  // first do a search because that is required for a selection
  XmlSearch().search('anyText -> datestamp )
  GetRequest("metadata.select","id" -> 0, "selected" -> "add-all").execute()
  val correctSelection = GetRequest("metadata.select","selected" -> "status").execute().value.getXml.text.trim must_== "2"
  
  correctSelection
}
  lazy val download = {
    val request = new AbstractGetRequest[Any,ZipFile]("mef.export", ZipFileValueFactory, SP("format" -> "full"), SP("version" -> "2")) {}
    request.execute().value
  }

  def entries = download.entries.asInstanceOf[Enumeration[ZipEntry]].asScala.toList

  def che = {
    val md = entries.filter(_.getName endsWith "metadata.xml")
    val mdXml = XML.loadString(Resource.fromInputStream(download.getInputStream(md.head)).string)
    println(mdXml)
	((mdXml \\ "extent" \@ "xlink:href") must beEmpty)
  }

  
}
