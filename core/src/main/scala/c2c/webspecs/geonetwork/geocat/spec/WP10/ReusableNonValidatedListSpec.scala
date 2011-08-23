 package c2c.webspecs
package geonetwork
package geocat
package spec
package WP10

import c2c.webspecs.geonetwork.geocat.GeocatSpecification
import org.specs2.specification.Step
import org.specs2.matcher.Matcher
import c2c.webspecs.geonetwork.edit._
import org.specs2.execute.Result

class ReusableNonValidatedListSpec extends GeocatSpecification { def is =
  "List non validated objects".title                            ^
  "This specification tests the Server API for obtaining the list of non-validated objects" ^ Step(setup) ^
  "First create some non-validated objects" ^ Step(CreateNonValidatedObjects) ^
      "Listing the ${contacts} should list the nonvalidated contact" ! containsNonValidated ^
      "The edit link for ${contacts} should be valid" ! editLink ^
      "The sharedobject URL for ${contacts} should be a local:// url" ! localURL ^ endp ^
      "Listing the ${extents} should list the nonvalidated extent" ! containsNonValidated ^
      "The edit link for ${extents} should be valid" ! editLink ^
      "The sharedobject URL for ${extents} should be a local:// url" ! localURL ^ endp ^
      "Listing the ${formats} should list the nonvalidated format" ! containsNonValidated ^
      "The edit link for ${formats} should be valid" ! editLink ^
      "The sharedobject URL for ${formats} should be a local:// url" ! localURL ^ endp ^
      "Listing the ${keywords} should list the nonvalidated keywords" ! containsNonValidated ^
      "The edit link for ${keywords} should be valid" ! editLink ^ endp ^
      "The sharedobject URL for ${keywords} should be a local:// url" ! localURL ^ endp ^
   "Next we will create a metadata with each reusable object" ^ Step(createMetadata) ^
      "Finding the referenced metadata for ${contacts} should find the newly created metadata" ! findReferenced ^
      "Finding the referenced metadata for ${extents} should find the newly created metadata" ! findReferenced ^
      "Finding the referenced metadata for ${formats} should find the newly created metadata" ! findReferenced ^
      "Finding the referenced metadata for ${keywords} should find the newly created metadata" ! findReferenced ^
                                                                                                  Step(tearDown)

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
    contactSpec.contactAdd()
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

  val containsNonValidated = (s: String) => {
    val listing = list(s)
    val uuid = lookupUUID(s)
    listing.value find { _.description contains uuid.toString } must beSome
  }

  val editLink = (s: String) => {
    val listing = list(s)
    val url = extract1(s) match {
      case "contacts" => "shared.user.edit"
      case "extents" => "extent.edit"
      case "formats" => "format.admin"
      case "keywords" => "thesaurus.editelement"
    }
    listing.value.map(_.url.get) must =~(url).forall
  }

  val localURL = (s: String) => {
    val listing = list(s)

    listing.value.map(_.url.get) must startWith("local://").forall
  }

  lazy val createMetadata = {
    val importMdId = Id(ImportMetadata.defaults(uuid, "/geocat/data/bare.iso19139.che.xml", false, getClass)._2().value.id)
    registerNewMd(importMdId)
    nonValidatedKeywords.value find { _.description contains keywordSpec.uuid.toString } foreach { obj =>
      val id = keywordHref.split("&").find(_ startsWith "id=").get.decode.split("#")(1)
      AddXlink.request(AddKeywordXLink(GeocatConstants.NON_VALIDATED_THESAURUS, GeocatConstants.KEYWORD_NAMESPACE, id, AddSites.descriptiveKeywords))(importMdId)
    }
    nonValidatedContacts.value find { _.description contains contactSpec.uuid.toString } foreach { contact =>
      AddXlink.request(AddContactXLink(Id(contact.id), AddSites.contact))(importMdId)
    }
    nonValidatedExtents.value find { _.description contains extentSpec.uuid.toString } foreach { obj =>
      AddXlink.request(AddExtentXLink(StandardSharedExtents.Custom("gn:non_validated", obj.id), true, AddSites.extent))(importMdId)
    }
    nonValidatedFormats.value find { _.description contains formatSpec.uuid.toString } foreach { obj =>
      AddXlink.request(AddFormatXLink(Id(obj.id), AddSites.distributionFormat))(importMdId)
    }
    importMdId
  }

  val findReferenced = (s: String) => {
    val listing = list(s).value
    val specificUuid = lookupUUID(s)

    val obj = listing find { _.description contains specificUuid.toString } get

    val referenced = ListReferencingMetadata(obj.id.toInt, obj.objType)().value
    val correctId = referenced.map(_.mdId) must contain(createMetadata.id.toInt)
    val correctTitle = referenced.map(_.title) must contain(uuid.toString).atLeastOnce
    correctId and correctTitle
  }
}