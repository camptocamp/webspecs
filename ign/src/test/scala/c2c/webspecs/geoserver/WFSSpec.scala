package c2c.webspecs
package geoserver
import c2c.webspecs.WebSpecsSpecification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.Matcher
import org.specs2.execute.Result

@RunWith(classOf[JUnitRunner])
class WFSSpec extends GeoserverSpecification {
  // TODO test WFSDispatcher with all of these as well as just /wfs
  def isImpl =
    "WFS Spec".title ^
      "This Spec test WFS API" ^
      "Wfs getCapabilities 1.0.0 must be valid" ! capabilities100 ^
      "Wfs getCapabilities 1.1.0 must be valid" ! capabilities110 ^
      "Wfs DescribeFeatureType must be valid" ! describeFeatureType ^
      "Wfs GetFeature can get at least one features" ! getFeature

  def capabilities100 = {
    val response = GetWfsRequest("1.0.0", "GetCapabilities").execute()
    (response must haveA200ResponseCode) and
      (response.value.getXml must \\("Capability")) and
      (response.value.getXml must \\("GetCapabilities")) and
      (response.value.getXml must \\("DescribeFeatureType")) and
      (response.value.getXml must \\("GetFeature")) and
      (response.value.getXml must \\("Transaction")) and
      (response.value.getXml must \\("LockFeature")) and
      (response.value.getXml must \\("GetFeatureWithLock")) and
      (response.value.getXml must \\("FeatureTypeList")) and
      (response.value.getXml must \\("FeatureType")) and
      (response.value.getXml must \\("Filter_Capabilities"))
  }
  def capabilities110 = {
    val response = GetWfsRequest("1.1.0", "GetCapabilities").execute()
    (response must haveA200ResponseCode) and
      (response.value.getXml must \\("WFS_Capabilities")) and
      (response.value.getXml must \\("ServiceIdentification")) and
      (response.value.getXml must \\("ServiceProvider")) and
      (response.value.getXml must \\("ServiceContact")) and
      (response.value.getXml must \\("OperationsMetadata")) and
      (response must haveOperation("GetCapabilities")) and
      (response must haveOperation("DescribeFeatureType")) and
      (response must haveOperation("GetFeature")) and
      (response must haveOperation("GetGmlObject")) and
      (response must haveOperation("LockFeature")) and
      (response must haveOperation("GetFeatureWithLock")) and
      (response must haveOperation("Transaction")) and
      (response.value.getXml \\ "Get" \@ "xlink:href" must contain("://" + Properties.testServer).forall) and
      (response.value.getXml \\ "Post" \@ "xlink:href" must contain("://" + Properties.testServer).forall) and
      (response.value.getXml must \\("FeatureTypeList")) and
      (response.value.getXml must \\("FeatureType")) and
      (response.value.getXml must \\("Filter_Capabilities"))
  }

  lazy val typeNames = {
    val response = GetWfsRequest("1.1.0", "GetCapabilities").execute()
    (response.value.getXml \\ "FeatureType" \\ "Name").map(_.text.trim)
  }
  def describeFeatureType = {
    def describe(typeName: String) = {
      val response = GetWfsRequest("1.1.0", "DescribeFeatureType", "TypeName" -> typeName).execute()
      ((response must haveA200ResponseCode) and
      	(response.value.getText must contain("schema")) and
      	(response.value.getXml must \\("schema")) and
      	(response.value.getXml must \\("complexType")) and
      	(response.value.getXml must \\("extension")) and
      	(response.value.getXml \\ "complexContent" \ "extension" \ "sequence" must \("element")))
    }
    typeNames.foldLeft(success:Result){ (result, next) => result and describe(next)}
  }
  def getFeature = {
    def getFirstFeature(typeName:String) = {
      val xml = 
        <wfs:GetFeature 
    		  service="WFS" 
    		  version="1.1.0"
    		  maxFeatures="1"
    		  xmlns:topp="http://www.openplans.org/topp" xmlns:wfs="http://www.opengis.net/wfs" xmlns:ogc="http://www.opengis.net/ogc" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.opengis.net/wfs
    		  http://schemas.opengis.net/wfs/1.1.0/wfs.xsd">
        <wfs:Query typeName={typeName}>
        </wfs:Query>
      </wfs:GetFeature>
      val featureTypeName = typeName.split(":").toList match {
        case _ :: name :: Nil => name
        case name :: Nil => name
        case _ => throw new IllegalStateException("boom")
      }
      val response = XmlPostRequest(GeoserverRequests.url(None, "wfs"),xml).execute()
      (response.value.getXml must \\("FeatureCollection")) and
      	(response.value.getXml must \\("featureMembers")) and
      	(response.value.getXml \\ featureTypeName must haveSize(1))
    }
    typeNames.foldLeft(success:Result){ (result, next) => result and getFirstFeature(next)}
  }
}