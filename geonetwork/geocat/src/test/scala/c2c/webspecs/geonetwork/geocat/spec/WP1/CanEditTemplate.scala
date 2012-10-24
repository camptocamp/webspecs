package c2c.webspecs
package geonetwork
package geocat
package spec.WP1

import edit._
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CanEditTemplate  extends GeocatSpecification {  def is =

  "CanEditTemplate".title      ^ Step(setup) ^
      "Import a metadata as a template" ^ Step(importTemplate) ^
      "The template can be edited without errors" ! canEdit ^ Step(tearDown)


  var id:Id = _
  def importTemplate = {
    val request = ImportMetadata.defaults(uuid,"/geocat/data/bare.iso19139.che.xml",false,getClass)._2.copy(template=true)
    val response = request.execute()
    id = Id(response.value.id)
    registerNewMd(id)
    
    response must haveA200ResponseCode
  }
 
  def canEdit = {
    def title(n:xml.NodeSeq) = n \\ "CI_Citation" \ "title" \ "PT_FreeText" \ "textGroup" \ "LocalisedCharacterString"
    val editValue = StartEditing().execute(id).value
    val lcs = (title(editValue.getXml) \ "element" \@ "ref").head
    val updateResponse = UpdateMetadata(true,false,("_"+lcs) -> "New Title").execute(editValue)
    println(title(updateResponse.value.getXml))
    (updateResponse must haveA200ResponseCode) and
    	(title(updateResponse.value.getXml).head.text.trim must_== "New Title")
  } 
}