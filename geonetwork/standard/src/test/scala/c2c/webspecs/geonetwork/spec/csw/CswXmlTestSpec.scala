package c2c.webspecs
package geonetwork
package spec.csw

import org.junit.runner.RunWith
import scala.xml._
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step


@RunWith(classOf[JUnitRunner])
class CswXmlTestSpec extends GeonetworkSpecification {
  def is =
    "GeoNetwork-trunk XML testsuite for CSW server".title ^ Step(setup) ^
      "Load a sample metadata" ^ Step(importMetadataId) ^
      "Perform the csw request in the XML file : ${csw-DescribeRecordWithMD_Metadata}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-DescribeRecordWithMultipleTypeName}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-DescribeRecord}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetCapabilitiesSections}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetCapabilities}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetDomainParameterName}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetDomainPropertyName}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordByIdFraIsoRecord}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordByIdIsoRecord}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordById}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsCQLAny}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsCQLEquals}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsElementName}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsFilterGeoBbox2Equals}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsFilterGeoBboxEquals}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsFilterGeoBboxForMoreThan180degrees}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsFilterGeoBoxIntersects}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsFilterGeoBox}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsFilterGeoEnvelope}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsFilterIsEqualToOnKeyword}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsFilterIsEqualToOnProtocol}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsFilterIsEqualToPhraseOnTitle}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsFilterIsLikeOnAny}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsFilterIsLikePhraseOnAbstract}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsFilterRangeCswIsoRecord}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsFilterService}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsNoFilterCswIsoRecord}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsNoFilterFraIsoRecord}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsNoFilterIsoRecord}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsNoFilterResultsWithSummary}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsNoFilterResults}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsNoFilterValidate}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsNoFilter}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-GetRecordsSortBy}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-Harvest}" ! ExecuteXmlTest ^
      "Perform the csw request in the XML file : ${csw-OwsException}" ! ExecuteXmlTest ^
      end ^ Step(tearDown)

  lazy val importMetadataId = {
    val mdId = importMd(1, "/geonetwork/data/valid-metadata.iso19139.xml", uuid.toString).head

    val md = GetRawMetadataXml.execute(mdId).value.getXml
    val response = (md \\ "fileIdentifier").text.trim
    response
  }

  def ExecuteXmlTest = (desc: String) => {
    val xmlFile = extract1(desc) + ".xml"
    val (xmlResource, _) = ResourceLoader.loadDataFromClassPath("/geonetwork/data/cswXmlTests/" + xmlFile, getClass, uuid)
    val cswTestRequest = XmlPostRequest("csw", XML.loadString(xmlResource)).execute()

    // In some cases (a failure is expected), we do not want to trigger errors by parsing XML
    if (!xmlFile.contains("FraIsoRecord") && (!xmlFile.contains("GetRecordsFilterGeoBbox2Equals")) && (!xmlFile.contains("OwsException")))
      cswTestRequest.value.getXml

    (cswTestRequest must haveA200ResponseCode)

    // TODO checking failing responses
  }

}
