package c2c.webspecs
package geonetwork
package geocat
package spec
package WP10

import edit._
import AddSites.AddSite

abstract class AbstractSharedObjectSpec extends GeocatSpecification {
  
  // reuse the add reusable objects code from the other specs
  val contactSpec = new WP3.AddSharedContactsSpec()
  val formatSpec = new WP3.AddSharedFormatSpec()
  val extentSpec = new WP3.AddSharedExtentsSpec()
  val keywordSpec = new WP3.AddSharedKeywordsSpec()

  var keywordHref: String = _

  override def extraTeardown(teardownContext: ExecutionContext): Unit = {
    super.extraTeardown(teardownContext)
    contactSpec.tearDown
    formatSpec.tearDown
    extentSpec.tearDown
    keywordSpec.tearDown
  }

  def CreateNonValidatedObjects = {
    config.adminLogin()
    contactSpec.contactAdd(false)()
    formatSpec.formatAdd(formatSpec.version)()
    extentSpec.extentAdd("${" + ExtentFormat.gmd_bbox + "}" + "${0}")
    keywordHref = (keywordSpec.keywordAdd("deValue" + uuid)().value \\ "descriptiveKeywords" \@ "xlink:href")(0)
  }

  lazy val nonValidatedContacts = ListNonValidatedContacts()
  lazy val nonValidatedExtents = ListNonValidatedExtents()
  lazy val nonValidatedFormats = ListNonValidatedFormats()
  lazy val nonValidatedKeywords = ListNonValidatedKeywords()

  def list(s: String) = extract1(s) match {
    case "contacts" => nonValidatedContacts
    case "extents" => nonValidatedExtents
    case "formats" => nonValidatedFormats
    case "keywords" => nonValidatedKeywords
  }

  def lookupUUID(s: String) = extract1(s) match {
    case "contacts" => contactSpec.uuid
    case "extents" => extentSpec.uuid
    case "formats" => formatSpec.uuid
    case "keywords" => keywordSpec.uuid
  }

  type SharedStructure = {
    def id:String
    def objType:SharedObjectTypes.SharedObjectType
  }
  def findSharedContact:Option[SharedStructure] = nonValidatedContacts.value find { _.description contains contactSpec.uuid.toString }
  def findSharedKeyword:Option[SharedStructure] = nonValidatedKeywords.value find { _.description contains keywordSpec.uuid.toString }
  def findSharedFormat:Option[SharedStructure] = nonValidatedFormats.value find { _.description contains formatSpec.uuid.toString }
  def findSharedExtent:Option[SharedStructure] = nonValidatedExtents.value find { _.description contains extentSpec.uuid.toString }
  
  lazy val createMetadata = {
    val importMdId = Id(ImportMetadata.defaults(uuid, "/geocat/data/bare.iso19139.che.xml", false, getClass)._2().value.id)
    registerNewMd(importMdId)
    findSharedKeyword foreach { obj =>
      val id = keywordHref.split("&").find(_ startsWith "id=").get.decode.split("#")(1)
      AddXlink.request(AddKeywordXLink(GeocatConstants.NON_VALIDATED_THESAURUS, GeocatConstants.KEYWORD_NAMESPACE, id, AddSites.descriptiveKeywords))(importMdId)
    }
    findSharedContact foreach { contact =>
      AddXlink.request(AddContactXLink(Id(contact.id), AddSites.contact))(importMdId)
    }
    findSharedExtent foreach { obj =>
      AddXlink.request(AddExtentXLink(StandardSharedExtents.Custom("gn:non_validated", obj.id), true, AddSites.extent))(importMdId)
    }
    findSharedFormat foreach { obj =>
      AddXlink.request(AddFormatXLink(Id(obj.id), AddSites.distributionFormat))(importMdId)
    }
    importMdId
  }

  def getMetadataWithXLinks = {
    val response = GetEditingMetadataXml(createMetadata)
    assert(response.basicValue.responseCode == 200)
    response.value
  }

  def validateCorrectRejection(deletedId: String, link: AddSite) = {
    val elem = getMetadataWithXLinks.getXml \\ link.name
    val href = elem \@ "xlink:href"

    val updatedHref = href must be_==("local://xml.reusable.deleted?id=" + deletedId).foreach

    DeleteSharedObject(deletedId)()
    val isDeletedAfterDeletion = ListDeletedSharedObjects().value.map(_.id) must not contain (deletedId)
    updatedHref and isDeletedAfterDeletion
  }

}