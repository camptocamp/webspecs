package c2c.webspecs.geonetwork.spec.csw

import c2c.webspecs._
import geonetwork._
import csw._

import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith (classOf[JUnitRunner])
class CswTransactionUpdateSpec extends GeonetworkSpecification {
  def is =
    Step(setup) ^ sequential ^
    "Insert a metadata using CSW Insert" ^ insertMetadata ^
      "Verify metadata has been inserted" ! metadataIsOriginalMetadata ^endp^
    "UpdateMetadata title"                       ^ updateMetadataTitle ^
      "Verify title is updated"     ! titleHasBeenUpdated ^endp^
    "Reset metadata" ^ resetMetadata ^
      "Verify that metadata has been reset" ^ metadataIsOriginalMetadata ^endp^
    "UpdateMetadata abstract"                       ^ updateMetadataAbstract ^
      "Verify abstract is updated"     ! abstractHasBeenUpdated ^endp^
    "UpdateMetadata title using an xpath expression" ^ updateMetadataTitleUsingXPath ^
      "Verify title is updated"     ! titleHasBeenUpdated ^endp^
    "Reset metadata" ^ resetMetadata ^
      "Verify that metadata has been reset" ^ metadataIsOriginalMetadata ^endp^
    "Reset metadata" ^ resetMetadata ^
      "Verify that metadata has been reset" ^ metadataIsOriginalMetadata ^endp^
    "UpdateMetadata twice quickly in serial"      ^ updateMetadataSerial ^
      "Verify that both updates have been accomplished" ! metadataHasBeenUpdated ^endp^
    "Reset metadata" ^ resetMetadata ^
      "Verify that metadata has been reset" ^ metadataIsOriginalMetadata ^endp^
    "UpdateMetadata twice quickly in parallel"      ^ updateMetadataParallel ^
      "Verify that both updates have been accomplished" ! metadataHasBeenUpdated ^endp^
    "Delete metadata using CSW Delete" ^ deleteMetadata ^
      "Verify that metadata has been deleted" ! metadataHasBeenDeleted ^ Step (tearDown)

  override def extraTeardown(teardownContext: ExecutionContext): Unit = {
    super.extraTeardown(teardownContext)
    GetRequest("metadata.delete", "uuid" -> uuid).execute()(teardownContext,uriResolver)
  }

  def insertMetadata = Step {
    config.adminLogin.execute()
    val response = CswTransactionInsert(sampleData).execute()
    response must haveA200ResponseCode
  }

  def metadataIsOriginalMetadata = {
    Thread.sleep(500) // let geonetwork finishing indexing
    val response = CswGetRecordById(uuid.toString).execute()

    (response must haveA200ResponseCode) and
      ((response.value.getXml \\ "MD_Metadata") must haveSize(1)) and
      (titleVal(response) must_== initialTitle)
  }

  def updateMetadataTitle = Step{
    CswTransactionUpdate(uuid.toString, "Title" -> (updatedTitle)).execute() must haveA200ResponseCode
  }
  def updateMetadataTitleUsingXPath = Step{
    CswTransactionUpdate(uuid.toString, "gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString" -> (updatedTitle)).execute() must haveA200ResponseCode
  }
  def titleHasBeenUpdated = titleVal(CswGetRecordById(uuid.toString).execute()) must_== updatedTitle

  def updateMetadataAbstract = Step{
    CswTransactionUpdate(uuid.toString, "Abstract" -> (updatedAbstract)).execute() must haveA200ResponseCode
  }
  def abstractHasBeenUpdated = abstractVal(CswGetRecordById(uuid.toString).execute()) must_== updatedAbstract

  def updateMetadataSerial = Step{
    val results = List(CswTransactionUpdate(uuid.toString, "Title" -> (updatedTitle)),
         CswTransactionUpdate(uuid.toString, "Abstract" -> (updatedAbstract))).map(_.execute())

    results must haveA200ResponseCode.forall
  }
  def resetMetadata = Step{ CswTransactionFullUpdate(sampleData).execute() must haveA200ResponseCode  }
  def updateMetadataParallel = Step{
    val results = List(CswTransactionUpdate(uuid.toString, "Title" -> (updatedTitle)),
      CswTransactionUpdate(uuid.toString, "Abstract" -> (updatedAbstract))).par.map(_.execute())

    results.seq must haveA200ResponseCode.forall
  }

  def metadataHasBeenUpdated = {
    val response = CswGetRecordById(uuid.toString).execute()

    (response must haveA200ResponseCode) and
      (titleVal(response) must_== updatedTitle) and
      (abstractVal(response) must_== updatedAbstract)
  }
  def deleteMetadata = Step{CswTransactionDelete(uuid.toString).execute()}
  def metadataHasBeenDeleted = {
    val response = CswGetRecordById(uuid.toString).execute()

    (response must haveA200ResponseCode) and
      ((response.value.getXml \\ "MD_Metadata") must beEmpty)
  }

  val initialTitle = "initial title"
  val initialAbstract = "initial abstract"
  val updatedTitle = "updated title"
  val updatedAbstract = "updated abstract"

  def titleVal(response:Response[XmlValue]) = (response.value.getXml \\ "MD_DataIdentification" \\ "citation" \\ "title" \\ "CharacterString").text
  def abstractVal(response:Response[XmlValue]) = (response.value.getXml \\ "MD_DataIdentification" \\ "abstract" \\ "CharacterString").text

  val sampleData =
  <gmd:MD_Metadata xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gts="http://www.isotc211.org/2005/gts" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gml="http://www.opengis.net/gml">
    <gmd:fileIdentifier>
      <gco:CharacterString>{uuid}</gco:CharacterString>
    </gmd:fileIdentifier>
    <gmd:language>
        <gmd:LanguageCode codeList="http://www.loc.gov/standards/iso639-2/" codeListValue="eng"/>
    </gmd:language>
    <gmd:characterSet>
        <gmd:MD_CharacterSetCode codeListValue="utf8" codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/Codelist/ML_gmxCodelists.xml#MD_CharacterSetCode"/>
    </gmd:characterSet>
    <gmd:dateStamp>
      <gco:DateTime>2012-01-16T17:06:13</gco:DateTime>
    </gmd:dateStamp>
    <gmd:identificationInfo>
      <gmd:MD_DataIdentification>
        <gmd:citation>
          <gmd:CI_Citation>
            <gmd:title>
              <gco:CharacterString>{initialTitle}</gco:CharacterString>
            </gmd:title>
          </gmd:CI_Citation>
        </gmd:citation>
        <gmd:abstract>
          <gco:CharacterString>{initialAbstract}</gco:CharacterString>
        </gmd:abstract>
        <gmd:spatialRepresentationType>
            <gmd:MD_SpatialRepresentationTypeCode codeListValue="vector" codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/Codelist/ML_gmxCodelists.xml#MD_SpatialRepresentationTypeCode"/>
        </gmd:spatialRepresentationType>
        <gmd:language>
            <gmd:LanguageCode codeList="http://www.loc.gov/standards/iso639-2/" codeListValue="eng"/>
        </gmd:language>
      </gmd:MD_DataIdentification>
    </gmd:identificationInfo>
  </gmd:MD_Metadata>

}