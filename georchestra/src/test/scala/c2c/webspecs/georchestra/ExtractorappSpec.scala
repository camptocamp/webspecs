package c2c.webspecs
package georchestra

import net.liftweb.json._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import net.liftweb.json.JsonDSL._

@RunWith(classOf[JUnitRunner])
class ExtractorappSpec extends GeorchestraSpecification {
  def isImpl =
    "Extractorapp Specification".title ^
      "This specification tests various Mapfishapp services" ^
      "Extractorapp should be fail to extract protected coverages when not logged in" ! noLoginCoverage ^
      "Extractorapp should be fail to extract protected coverages when not logged in" ! noLoginVector ^
      "Extractorapp should be able to extract protected coverages in ${geotiff} format" ! extractCoverage ^
      "Extractorapp should be able to extract protected coverages in ${ecw} format" ! extractCoverage ^
      "Extractorapp should be able to extract protected coverages in ${jp2ecw} format" ! extractCoverage ^
      "Extractorapp should be able to extract protected vector layers ${shp}" ! extractVector

  object BBox {
    def apply(spec: String): BBox = {
      val Array(minx, maxx, miny, maxy, epsgCode) = spec.split(",")
      BBox(minx.toDouble, maxx.toDouble, miny.toDouble, maxy.toDouble, epsgCode)
    }
  }
  case class BBox(minx: Double, maxx: Double, miny: Double, maxy: Double, epsgCode: String)
  def extractJSON(
    layerName: String, owsType: String, owsURL: String,
    bbox: BBox, vectorFormat: String = "shp", rasterFormat: String = "geotiff"): JObject = {
    ("emails" -> List[String]()) ~
      ("globalProperties" -> (
        ("projection" -> bbox.epsgCode) ~
        ("resolution" -> 0.5) ~
        ("rasterFormat" -> rasterFormat) ~
        ("vectorFormat" -> vectorFormat) ~
        ("bbox" ->
          ("srs" -> bbox.epsgCode) ~
          ("value" -> List(bbox.minx, bbox.maxx, bbox.miny, bbox.maxy))))) ~
          ("layers" -> List(
            ("projection" -> JNull) ~
              ("resolution" -> JNull) ~
              ("format" -> JNull) ~
              ("owsUrl" -> owsURL) ~
              ("owsType" -> owsType) ~
              ("layerName" -> layerName)))
  }

  def noLoginCoverage = {
    login.LogoutRequest().execute()
    val response = doExtractCoverage("geotiff")
    response must not (haveA200ResponseCode)
  }
  def extractCoverage = (s: String) => {
    val format = extract1(s)
    config.login.execute()
    val response = doExtractCoverage(format)
    response must haveA200ResponseCode
  }
  def doExtractCoverage(format: String) = {
    val bbox = BBox(Properties("extractor.wcs.bbox").get)
    val layerName = Properties("geoserver.protected.coverage.layer").get
    val jsonData = extractJSON(layerName, "WCS", "http://" + Properties.testServer + "/geoserver/wcs", bbox, rasterFormat = format)
    JsonPostRequest("extractorapp/extractor/test/initiate", jsonData).execute()
  }
  
  def noLoginVector = {
    login.LogoutRequest().execute()
    val response = doExtractVector("shp")
    response must not (haveA200ResponseCode)
  }
  def extractVector = (s: String) => {
    val format = extract1(s)
    config.login.execute()
    val response = doExtractVector(format)
    response must haveA200ResponseCode
  }
  def doExtractVector(format: String) = {
    val bbox = BBox(Properties("extractor.wfs.bbox").get)
    val layerName = Properties("geoserver.protected.vector.layer").get
    val jsonData = extractJSON(layerName, "WFS", "http://" + Properties.testServer + "/geoserver/wfs", bbox, vectorFormat = format)
    JsonPostRequest("extractorapp/extractor/test/initiate", jsonData).execute()

  }

}