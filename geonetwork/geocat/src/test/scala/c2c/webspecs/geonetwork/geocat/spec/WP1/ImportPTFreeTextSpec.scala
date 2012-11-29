package c2c.webspecs
package geonetwork
package geocat
package spec.WP1

import csw._
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ImportPTFreeTextSpec  extends GeocatSpecification {  def is =

  "A bug has been found when importing certain metadata.  PT_FreeText elements are being incorrectly created."      ^ Step(setup) ^ sequential ^
      "wms bgdi metadata is correctly imported (and valid)" ! importWmsBgdiMD ^
      "the metadata must be able to be published with the privileges UI" ! canPublishWithMetadataAdminUI ^ Step(tearDown)


  var id:String = _
  def importWmsBgdiMD = {
    val request = ImportMetadata.defaults(uuid,"/geocat/data/metadata-ptfree-text-invalidating-bug.xml",true,getClass)._2
    val response = request.execute()
    registerNewMd(Id(response.value.id))
    id = response.value.id
    
    response must haveA200ResponseCode
  }
 
  def canPublishWithMetadataAdminUI = {
    config.adminLogin.execute()
    val response = GetRequest("eng/metadata.admin.form", 'id -> id).execute()
    val xml = response.value.getXml
    println(xml)
    val checkbox = xml \\ "input" filter {b => (b @@ "name") == List("_-1_0") }
    
    (checkbox aka "checkboxs" must not beEmpty) and ((checkbox \@ "disabled") aka "disabled attribute" must beEmpty)
  } 
}