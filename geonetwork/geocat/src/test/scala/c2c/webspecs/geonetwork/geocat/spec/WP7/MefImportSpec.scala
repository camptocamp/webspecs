package c2c.webspecs
package geonetwork
package geocat
package spec.WP7

import java.util.Enumeration
import collection.JavaConverters._
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
import java.util.zip.ZipFile
import scalax.file.Path
import scalax.file.defaultfs.DefaultPath
import java.io.InputStreamReader
import scalax.io.Resource
import java.util.zip.ZipEntry
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream
import java.io.File
import java.io.FileOutputStream
import org.apache.http.entity.mime.content.ByteArrayBody

@RunWith(classOf[JUnitRunner])
class MefImportSpec extends GeocatSpecification { def is =
    sequential ^ "This spec tests importing metadata from an MEF file" 								 ^ Step(setup) ^
    "Import a metadata"                                                                              ^ importMEF.toGiven ^
    "verify the response is a 200"					                                                 ^ a200ResponseThen.narrow[Response[IdValue]] ^
    "verify the metadata has been imported exactly once"                                             ^ importedCorrectly.toThen ^
                                                                                                       Step(tearDown)
                                                                                                       
  val importMEF = (s:String) => {
    
    val data = Resource.fromClasspath("geocat/data/sampleMefFileOneMdThreeFormats.zip").byteArray
    val contentBody = new ByteArrayBody(data, "application/zip", "sampleMefFileOneMdThreeFormats.zip")
    val response = ImportMetadata(contentBody,ImportStyleSheets.NONE,false,config.groupId, fileType=ImportMdFileType.mef)()
    registerNewMd(response.value)
    response:Response[IdValue]
  }

  val importedCorrectly = (resp:Response[IdValue], s:String) => {
    val xml = CswGetRecordsRequest(PropertyIsEqualTo("_defaultTitle","Title_ImportMEFTextFile").xml)().value.getXml
    
    (xml \\ "@numberOfRecordsMatched").text.toInt must_== 1
  }
}