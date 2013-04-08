package c2c.webspecs
package geonetwork
package geocat
package spec
package WP10

import shared._
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
    config.adminLogin.execute()
    contactSpec.contactAdd(false)()
    formatSpec.formatAdd(formatSpec.version)()
    extentSpec.extentAdd("${" + ExtentFormat.gmd_bbox + "}" + "${0}")
    keywordHref = (keywordSpec.keywordAdd("deValue" + keywordSpec.uuid)().value \\ "descriptiveKeywords" \@ "xlink:href")(0)
  }

  lazy val nonValidatedContacts = ListNonValidatedContacts.execute()
  lazy val nonValidatedExtents = ListNonValidatedExtents.execute()
  lazy val nonValidatedFormats = ListNonValidatedFormats.execute()
  lazy val nonValidatedKeywords = ListNonValidatedKeywords.execute()

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
    val importMdId = importMd(1,"/geocat/data/bare.iso19139.che.xml", uuid.toString).head

    findSharedKeyword foreach { obj =>
      val id = keywordHref.split("&").find(_ startsWith "id=").get.decode.split("#")(1)
      val link = AddKeywordXLink(GeocatConstants.NON_VALIDATED_THESAURUS, GeocatConstants.KEYWORD_NAMESPACE, id, AddSites.descriptiveKeywords)
      AddXlink.execute(link, importMdId)
    }
    findSharedContact foreach { contact =>
      val link = AddContactXLink(Id(contact.id), AddSites.contact)
      AddXlink.execute(link, importMdId)
    }
    findSharedExtent foreach { obj =>
      val link = AddExtentXLink(StandardSharedExtents.Custom("gn:non_validated", obj.id), true, AddSites.extent)
      AddXlink.execute(link,importMdId)
    }
    findSharedFormat foreach { obj =>
      val link = AddFormatXLink(Id(obj.id), AddSites.distributionFormat)
      AddXlink.execute(link,importMdId)
    }
    importMdId
  }

  def getMetadataWithXLinks = {
    val response = GetEditingMetadataXml.execute(createMetadata)
    assert(response.basicValue.responseCode == 200)
    response.value
  }

  def validateCorrectRejection(deletedId: String, link: AddSite) = {
    val elem = getMetadataWithXLinks.getXml \\ link.name
    val href = elem \@ "xlink:href"

    val updatedHref = href must be_==("local://xml.reusable.deleted?id=" + deletedId).foreach

    DeleteSharedObject(deletedId).execute()
    val isDeletedAfterDeletion = ListDeletedSharedObjects.execute().value.map(_.id) must not contain (deletedId)
    updatedHref and isDeletedAfterDeletion
  }

}