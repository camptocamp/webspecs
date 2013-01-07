package c2c.webspecs.geonetwork.spec.edit

import c2c.webspecs._
import geonetwork._
import geonetwork.edit._
import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.execute.Result

/**
 * User: jeichar
 * Date: 1/19/12
 * Time: 4:21 PM
 */
@RunWith(classOf[JUnitRunner])
class AddRemoveOverviewSpec extends GeonetworkSpecification {
  def is =
    Step(setup) ^ sequential ^
      "Add data to test against"     ^ Step(importTestData) ^
      "StartEditing"				 ! startEditing ^
      "Add small ${scaled} thumbnail"     ! addSmallThumbnail
      "Replace thumbnail with small ${non-scaled} thumbnail" ! addSmallThumbnail ^
      "Replace thumbnail with large thumbnail ${scaled large} ${scaled small} thumbnail" ! addLargeThumbnail ^
      "Replace thumbnail with large thumbnail ${non-scaled large} ${scaled small} thumbnail" ! addLargeThumbnail ^
      "Replace thumbnail with large thumbnail ${scaled large} ${non-scaled small} thumbnail" ! addLargeThumbnail ^
      "Replace thumbnail with large thumbnail ${non-scaled large} ${non-scaled small} thumbnail" ! addLargeThumbnail ^
      Step (tearDown)

  // allow overriding imported metadata for specific apps like Geocat.ch
  def metadataToImport = "/geonetwork/data/valid-metadata.iso19139.xml"
  lazy val importTestData = importMd(1, metadataToImport, datestamp, ImportStyleSheets.NONE);
  // just an alias to make the spec read clearer
  def metadataId = importTestData(0)

  var editValue:EditValue = _
  
  def startEditing = {
    val result = StartEditing().execute(metadataId)
    editValue = result.value
    result must haveA200ResponseCode
  }
  
  val addSmallThumbnail = (spec:String) => {
    val scaling = extract1(spec) match {
      case "scaled" => Some(ThumbnailScaling(180, true))
      case "non-scaled" => None
    }

    val img = ResourceLoader.loadImageFromClassPath("/geonetwork/SwitzerlandSketch-4.png", classOf[AddRemoveOverviewSpec])
    val response = SetSmallThumbnail(editValue,img, scaling = scaling).execute()
    editValue = response.value

    
    val imgSrcs = editValue.getXml \\ "img" \@ "src" filter (src => src.contains("resources.get") && src.contains("SwitzerlandSketch"))

    val imagesCanBeLoaded = imgSrcs.map {src =>
      val url = if (src.startsWith("http")) src else "http://"+Properties.testServer+"/"+src
      GetRequest(url).execute()
    }.foldLeft (success: Result) {(acc, next) => acc and (next must haveA200ResponseCode)}

    imagesCanBeLoaded and (response must haveA200ResponseCode) and (imgSrcs must not (beEmpty))
  }

  def addLargeThumbnail = (spec:String) => {
    val (large, small) = extract2(spec)
    
    val largeScaling = large match {
      case "scaled large" => Some(ThumbnailScaling(480, true))
      case "non-scaled large" => None
    }
    val smallScaling = small match {
    case "scaled small" => Some(ThumbnailScaling(180, true))
    case "non-scaled small" => None
    }
    
    val img = ResourceLoader.loadImageFromClassPath("/geonetwork/SwitzerlandSketch-4.png", classOf[AddRemoveOverviewSpec])
    val response = SetLargeThumbnail(editValue,img, largeScaling = largeScaling, smallScaling = smallScaling).execute()
    editValue = response.value

    val imgSrcs = editValue.getXml \\ "img" \@ "src" filter (src => src.contains("resources.get") && src.contains("SwitzerlandSketch"))

    val imagesCanBeLoaded = imgSrcs.map {src =>
      val url = if (src.startsWith("http")) src else "http://"+Properties.testServer+"/"+src
      GetRequest(url).execute()
    }.foldLeft (success: Result) {(acc, next) => acc and (next must haveA200ResponseCode)}

    imagesCanBeLoaded and (response must haveA200ResponseCode) and (imgSrcs must not (beEmpty))  
    
}
    
}