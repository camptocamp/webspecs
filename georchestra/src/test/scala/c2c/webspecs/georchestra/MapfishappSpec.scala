package c2c.webspecs
package georchestra

import net.liftweb.json._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MapfishappSpec extends GeorchestraSpecification {
  def isImpl =
    "Mapfishapp Specification".title ^
      "This specification tests various Mapfishapp services" ^
      "Style service should be able to classify when logged in" ! loggedInClassify

  def loggedInClassify = {
    config.login.execute()
    classify()
  }

  def classify() = {
    val json = """{
    |"wfs_url": "http://%s/geoserver/wfs/WfsDispatcher?REQUEST=GetCapabilities&SERVICE=WFS&VERSION=1.0.0",
    |"layer_name": "%s",
    |"attribute_name": "%s",
    |"type": "choropleths",
    |"symbol_type": "Polygon",
    |"class_count": 5,
    |"first_color": "#FFFFFF",
    |"last_color": "#497BD1"
    |}""".stripMargin.trim.replace("\n", "").format(Properties.testServer, Properties("geoserver.protected.vector.layer").get, Properties("geoserver.protected.vector.layer.choropleths.attribute").get)
    val response = StringPostRequest("mapfishapp/ws/sld/", json, "text/json; charset=utf-8").execute()
    (response must haveA200ResponseCode) and
      (response.value.getText must /("success" -> true)) and 
      downloadedSldIsValid(response.value.getText)
  }
  
  def downloadedSldIsValid(json:String) = {
    implicit val formats = DefaultFormats
    val filePath = 
      try {(parse(json) \\ "filepath").extract[String]}
      catch { case e: Throwable => throw new AssertionError("Failed to json: "+json)}
    
    val sldResponse = GetRequest("mapfishapp/"+filePath).execute()
    (sldResponse must haveA200ResponseCode) and 
      (sldResponse.value.getXml must \\("StyledLayerDescriptor"))
  }

}