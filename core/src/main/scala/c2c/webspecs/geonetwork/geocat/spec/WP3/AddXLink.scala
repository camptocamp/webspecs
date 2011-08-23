package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step
import c2c.webspecs.{XmlValue, Response}
import geonetwork.UserRef
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import c2c.webspecs.geonetwork.edit.AddSites
import c2c.webspecs.geonetwork.edit.StartEditing
import c2c.webspecs.geonetwork.edit.EndEditing
import org.specs2.specification.Fragments
import org.specs2.execute.Result
import org.specs2.specification.so

@RunWith(classOf[JUnitRunner]) 
class AddXLinksSpec extends GeocatSpecification { def is = 
  "AddLinks to metadata".title 																		^ 
  sequential 																						^
  "This specification adds XLinks to existing metadata "      										^ Step(setup) ^ 
  																									Step(ImportMdId) ^
  																									testType("contact") ^ 
  																									testType("format") ^ 
  																									testType("extent") ^ 
  																									testType("keyword") ^ 
                                                           											  Step(tearDown)

  def testType(name:String):Fragments = {
	"The following declarations specifies how to add and update xlinks of "+name+" shared objects"   					    ^
  	"Adding an ${"+name+"} XLink to metadata should result in the next access of the metadata containing the new contact"  	! so (addXLink) ^ 
  	"Updating shared ${"+name+"} should result in the metadata being updated in metadata as well" 							! so (updateXLink) ^ end  
  }

  lazy val ImportMdId = {
    val (_, importMd) = ImportMetadata.defaults(uuid, "/geocat/data/bare.iso19139.che.xml", false, getClass)
    importMd(None).value.id
  }
  val addXLink:PartialFunction[Any,Result] = {
      case "contact" => addContact
      case "format" => addFormat
      case "extent" => addExtent
      case "keyword" => addKeyword
  }
  val updateXLink:PartialFunction[Any,Result] = {
      case "contact" => updateContact
      case "format" => updateFormat
      case "extent" => updateExtent
      case "keyword" => updateKeyword
    }

  def addContact = {
    val addResponse = (StartEditing(ImportMdId) then AddContactXLink(Id(userFixture.id), AddSites.contact) startTrackingThen EndEditing then GetRawMetadataXml)()
    val (addValue, updatedMd) = addResponse.values
    val email = (addValue.newElement \\ "electronicMailAddress" \\ "CharacterString").text.trim
    val emailFromNew = (updatedMd.getXml \\ AddSites.contact.name \\ "electronicMailAddress" \\ "CharacterString").head.text.trim
    (email must_== userFixture.email) and
      (emailFromNew must_== userFixture.email)

  }

  def addFormat = {
    val addResponse = (StartEditing(ImportMdId) then AddFormatXLink(Id(formatFixture.id.toString), AddSites.distributionFormat) startTrackingThen EndEditing then GetRawMetadataXml)()
    val (addValue, updatedMd) = addResponse.values
    val format = (addValue.newElement \\ "name" \\ "CharacterString").text.trim
    val formatFromNew = (updatedMd.getXml \\ AddSites.distributionFormat.name \\ "name" \\ "CharacterString").head.text.trim
    (format must_== formatFixture.name) and
      (formatFromNew must_== formatFixture.name)

  }
  def addExtent = {
    val addResponse = (StartEditing(ImportMdId) then AddExtentXLink(StandardSharedExtents.KantonBern, true, AddSites.extent) startTrackingThen EndEditing then GetRawMetadataXml)()
    val (addValue, updatedMd) = addResponse.values
    val extentDesc = (addValue.newElement \\ "description" \\ "CharacterString").text.trim
    val extentDescFromNew = (updatedMd.getXml \\ AddSites.extent.name \\ "description" \\ "CharacterString").head.text.trim
    val polygons = addValue.newElement \\ "polygon" 
    val bbox = addValue.newElement \\ "EX_GeographicBoundingBox" 
    (extentDesc.toLowerCase() must contain ("bern")) and
      (extentDescFromNew.toLowerCase()  must contain ("bern")) and
      (polygons must haveSize(1)) and
      (bbox must haveSize(1))
  }

  def addKeyword = {
    import keywordFixture._
    val addKeywordRequest = AddKeywordXLink(thesaurus, namespace, id, AddSites.descriptiveKeywords)
    val addResponse = (StartEditing(ImportMdId) then addKeywordRequest startTrackingThen EndEditing then GetRawMetadataXml).apply()
    val (addValue, updatedMd) = addResponse.values
    val keyword = (addValue.newElement \\ "LocalisedCharacterString" ) map (_.text.trim)
    val keywordFromNew = (updatedMd.getXml \\ AddSites.descriptiveKeywords.name \\ "LocalisedCharacterString") map (_.text.trim)
    (keyword must contain(it,fr,de,en)) and
      (keywordFromNew must contain(it,fr,de,en))
  }

  def updateContact = {
    val newLastName = "newLastName"
    val updateUser = new UpdateSharedUser(userFixture.user.copy(surname = newLastName),true)
    (config.adminLogin then updateUser)()
    val md = GetRawMetadataXml(Id(ImportMdId)).value.getXml
    val lastName = (md \\ AddSites.contact.name \\ "individualLastName").text.trim
    lastName must_== newLastName 
  }
  
  def updateFormat = {
    val newVersion = "newVersion"
    (config.adminLogin then UpdateFormat(formatFixture.id.toString, formatFixture.name, newVersion))()
    val md = GetRawMetadataXml(Id(ImportMdId)).value.getXml
    val newFormatVersion = (md \\ AddSites.distributionFormat.name \\ "version" \\ "CharacterString").head.text.trim
    newFormatVersion must_== newVersion
  }
  def updateExtent = success:Result
  
  def updateKeyword = {
    val newde = "newDe"
    import keywordFixture._
    (config.adminLogin then UpdateKeyword(namespace, id, thesaurus, "DE" -> newde))()
    val md = GetRawMetadataXml(Id(ImportMdId)).value.getXml
    val keywordFromNew = (md \\ AddSites.descriptiveKeywords.name \\ "LocalisedCharacterString") map (_.text.trim)
      (keywordFromNew must contain(it,fr,newde,en))
  }
  override def extraTeardown(teardownContext: ExecutionContext): Unit = {
    DeleteMetadata(Id(ImportMdId))(teardownContext)
    super[GeocatSpecification].extraTeardown(teardownContext)
  }

  lazy val userFixture = GeocatFixture.sharedUser(false)
  lazy val keywordFixture = GeonetworkFixture.keyword(GeocatConstants.KEYWORD_NAMESPACE, GeocatConstants.NON_VALIDATED_THESAURUS)
  lazy val formatFixture = GeocatFixture.format
  override lazy val fixtures = Seq(userFixture, keywordFixture, formatFixture)
}