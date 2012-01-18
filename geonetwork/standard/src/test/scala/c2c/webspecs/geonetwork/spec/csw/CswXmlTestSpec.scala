package c2c.webspecs
package geonetwork
package spec.csw

import org.junit.runner.RunWith
import scala.xml._
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step

import c2c.webspecs.geonetwork.UserProfiles


@RunWith(classOf[JUnitRunner])
class CswXmlTestSpec extends GeonetworkSpecification {
  def is =
    "GeoNetwork-trunk XML testsuite for CSW server".title ^ Step(setup) ^
      "Loads a sample metadata" ^ Step(importMetadataId) ^
      "Process test using XML file : ${csw-DescribeRecordWithMD_Metadata}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-DescribeRecordWithMultipleTypeName}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-DescribeRecord}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetCapabilitiesSections}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetCapabilities}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetDomainParameterName}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetDomainPropertyName}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordByIdFraIsoRecord}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordByIdIsoRecord}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordById}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsCQLAny}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsCQLEquals}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsElementName}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsFilterGeoBbox2Equals}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsFilterGeoBboxEquals}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsFilterGeoBboxForMoreThan180degrees}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsFilterGeoBoxIntersects}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsFilterGeoBox}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsFilterGeoEnvelope}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsFilterIsEqualToOnKeyword}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsFilterIsEqualToOnProtocol}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsFilterIsEqualToPhraseOnTitle}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsFilterIsLikeOnAny}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsFilterIsLikePhraseOnAbstract}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsFilterRangeCswIsoRecord}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsFilterService}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsNoFilterCswIsoRecord}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsNoFilterFraIsoRecord}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsNoFilterIsoRecord}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsNoFilterOwn}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsNoFilterResultsWithSummary}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsNoFilterResults}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsNoFilterValidate}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsNoFilter}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-GetRecordsSortBy}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-Harvest}" ! ProceedXmlTest ^
      "Process test using XML file : ${csw-OwsException}" ! ProceedXmlTest ^
      end ^ Step(tearDown)

  lazy val importMetadataId = {
    val mdId = importMd(1, "/geonetwork/data/valid-metadata.iso19139.xml", uuid.toString).head

    val md = GetRawMetadataXml.execute(Id(mdId)).value.getXml
    val response = (md \\ "fileIdentifier").text.trim
    response
  }


  def ProceedXmlTest = (desc: String) => {
    val xmlFile = extract1(desc) + ".xml"
    val (xmlResource, _) = ResourceLoader.loadDataFromClassPath("/geocat/csw-xml-tests/" + xmlFile, getClass, uuid)
    val cswTestRequest = XmlPostRequest("csw", XML.loadString(xmlResource)).execute()

    // In some cases (a failure is expected), we do not want to trigger errors by parsing XML
    if (!xmlFile.contains("FraIsoRecord") && (!xmlFile.contains("GetRecordsFilterGeoBbox2Equals")) && (!xmlFile.contains("OwsException")))
      cswTestRequest.value.getXml

    (cswTestRequest must haveA200ResponseCode)

    // TODO checking failing responses
  }

}
