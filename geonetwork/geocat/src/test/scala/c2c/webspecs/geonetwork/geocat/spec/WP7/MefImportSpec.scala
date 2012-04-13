package c2c.webspecs
package geonetwork
package geocat
package spec.WP7

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import c2c.webspecs.{ Response, IdValue }
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import scalax.io.Resource
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
    val response = ImportMetadata(contentBody,ImportStyleSheets.NONE,false,config.groupId, fileType=ImportMdFileType.mef).execute()
    registerNewMd(response.value)
    response:Response[IdValue]
  }

  val importedCorrectly = (resp:Response[IdValue], s:String) => {
    val xml = CswGetRecordsRequest(PropertyIsEqualTo("_defaultTitle","Title_ImportMEFTextFile").xml).execute().value.getXml
    
    (xml \\ "@numberOfRecordsMatched").text.toInt must_== 1
  }
}