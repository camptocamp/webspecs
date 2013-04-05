package c2c.webspecs
package geonetwork
package geocat
package spec.WP15

import org.specs2.specification.Step
import c2c.webspecs.geonetwork.csw._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml.Elem

@RunWith(classOf[JUnitRunner])
class LocaleFixedInfoSpec extends GeocatSpecification { def is =
  "Xlink Add Extent".title ^
  "This spec verifies that the html returned when performing an ajax xlink add extent the extent correctly been added" ^ Step(setup) ^
  "Upload a metadata with incomplete locale information"                                                 ^ Step(uploadMd) ^
  "Fixed info transform should add id attribute to the PT_Locale elem" ! localeHasIdElem ^
  "Fixed info transform should add the codelist attribute to the LanguageCode elemt" ! languageCodeHasCodeListElem ^
  "Fixed info transform should add a characterEncoding elem" ! hasCharacterEncodingElem ^
  "Fixed info transform should add a id codelistValue attribute to MD_CharacterSetCode with utf-8 value" ! mdCharacterSetCodeHasCodeListValueElem ^
  "Fixed info transform should add a id codelist attribute to MD_CharacterSetCode" ! mdCharacterSetCodeHasCodeListElem ^ Step(tearDown)
  
  lazy val uploadMd = {
    val data: Elem = 
<che:CHE_MD_Metadata xmlns:srv="http://www.isotc211.org/2005/srv" xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:che="http://www.geocat.ch/2008/che" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gml="http://www.opengis.net/gml" gco:isoType="gmd:MD_Metadata">
  <gmd:fileIdentifier xmlns:xalan="http://xml.apache.org/xalan" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <gco:CharacterString>{uuid}</gco:CharacterString>
  </gmd:fileIdentifier>
  <gmd:language xmlns:xalan="http://xml.apache.org/xalan" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <gco:CharacterString>deu</gco:CharacterString>
  </gmd:language>
  <gmd:characterSet xmlns:xalan="http://xml.apache.org/xalan" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <gmd:MD_CharacterSetCode codeListValue="utf8" codeList="http://www.isotc211.org/2005/resources/codeList.xml#MD_CharacterSetCode"/>
  </gmd:characterSet>
  <gmd:hierarchyLevel xmlns:xalan="http://xml.apache.org/xalan" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <gmd:MD_ScopeCode codeListValue="dataset" codeList="http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode"/>
  </gmd:hierarchyLevel>
  <gmd:dateStamp xmlns:xalan="http://xml.apache.org/xalan" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <gco:DateTime>2012-07-24T11:10:33</gco:DateTime>
  </gmd:dateStamp>
  <gmd:metadataStandardName xmlns:xalan="http://xml.apache.org/xalan" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <gco:CharacterString>GM03_2</gco:CharacterString>
  </gmd:metadataStandardName>
  <gmd:locale xmlns:xalan="http://xml.apache.org/xalan" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <gmd:PT_Locale id="DE">
      <gmd:languageCode>
        <gmd:LanguageCode codeListValue="deu" codeList="http://www.isotc211.org/2005/resources/codeList.xml#LanguageCode">German</gmd:LanguageCode>
      </gmd:languageCode>
      <gmd:characterEncoding>
        <gmd:MD_CharacterSetCode codeListValue="" codeList="">UTF8</gmd:MD_CharacterSetCode>
      </gmd:characterEncoding>
    </gmd:PT_Locale>
  </gmd:locale>
  <gmd:locale>
    <gmd:PT_Locale>
      <gmd:languageCode>
        <gmd:LanguageCode codeListValue="roh"/>
      </gmd:languageCode>
    </gmd:PT_Locale>
  </gmd:locale>
  <gmd:identificationInfo xmlns:xalan="http://xml.apache.org/xalan" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <che:CHE_MD_DataIdentification gco:isoType="gmd:MD_DataIdentification"/>
  </gmd:identificationInfo>
</che:CHE_MD_Metadata>
    CswTransactionInsert(data).execute()
  }

  lazy val uploadedMd = CswGetRecordById(uuid.toString, outputSchema=geocat.OutputSchemas.CheIsoRecord).execute().value.getXml
  lazy val metadataElem = (uploadedMd \\ "CHE_MD_Metadata")
  lazy val locales = (metadataElem \\ "locale")
  lazy val ptLocales = (locales \ "PT_Locale")
  lazy val languageCode = (ptLocales \ "languageCode" \ "LanguageCode")
  lazy val characterEncoding = (ptLocales \ "characterEncoding")
  
  def localeHasIdElem = locales must \("PT_Locale", "id").forall
  def languageCodeHasCodeListElem = (locales \ "LanguageCode") must \("LanguageCode", "codeList" -> "http://www.isotc211.org/2005/resources/codeList.xml#LanguageCode").forall 
  def hasCharacterEncodingElem = ptLocales must \("characterEncoding").forall
  def mdCharacterSetCodeHasCodeListValueElem = characterEncoding must \("MD_CharacterSetCode", "codeListValue" -> "utf8").forall
  def mdCharacterSetCodeHasCodeListElem = characterEncoding must \("MD_CharacterSetCode", "codeList" -> "http://www.isotc211.org/2005/resources/codeList.xml#MD_CharacterSetCode").forall
 
  override def extraTeardown(teardownContext:ExecutionContext):Unit = CswTransactionDelete(uuid.toString).execute(teardownContext)
}