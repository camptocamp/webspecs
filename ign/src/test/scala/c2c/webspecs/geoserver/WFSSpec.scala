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
      "Wfs getCapabilities 2.0.0 must be valid" ! capabilities200 ^
      "Wfs 2.0.0 DescribeFeatureType must be valid for ${au:AdministrativeUnit}" ! describeFeatureType

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
  def capabilities200 = {
    val response = GetWfsRequest("2.0.0", "GetCapabilities").execute()
    (response must haveA200ResponseCode) and
      (response.value.getXml must \\("WFS_Capabilities")) and
      (response.value.getXml must \\("ServiceIdentification")) and
      (response.value.getXml must \\("ServiceProvider")) and
      (response.value.getXml must \\("ServiceContact")) and
      (response.value.getXml must \\("OperationsMetadata")) and
      (response must haveOperation("GetCapabilities")) and
      (response must haveOperation("DescribeFeatureType")) and
      (response must haveOperation("GetFeature")) and
      (response must haveOperation("LockFeature")) and
      (response must haveOperation("GetFeatureWithLock")) and
      (response must haveOperation("Transaction")) and
      (response.value.getXml \\ "Get" \@ "xlink:href" must contain("://" + Properties.testServer).forall) and
      (response.value.getXml \\ "Post" \@ "xlink:href" must contain("://" + Properties.testServer).forall) and
      (response.value.getXml must \\("FeatureTypeList")) and
      (response.value.getXml must \\("FeatureType")) and
      (response.value.getXml must \\("Filter_Capabilities"))
  }

  val describeFeatureType = (description: String) => {
    val typeName = extract1(description)
    val response = GetWfsRequest("2.0.0", "DescribeFeatureType", "TypeName" -> typeName).execute()
    ((response must haveA200ResponseCode) and
      (response.value.getText must contain("schema")) and
      (response.value.getXml must \\("schema")))
  }
  
}