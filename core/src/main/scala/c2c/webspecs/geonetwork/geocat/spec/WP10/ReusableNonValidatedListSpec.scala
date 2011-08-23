 package c2c.webspecs
package geonetwork
package geocat
package spec
package WP10

import c2c.webspecs.geonetwork.geocat.GeocatSpecification
import org.specs2.specification.Step
import org.specs2.matcher.Matcher

class ReusableNonValidatedListSpec extends GeocatSpecification { def is =
  "List non validated objects".title                            ^
  "This specification tests the Server API for obtaining the list of non-validated objects" ^ Step(setup) ^
  "First create some non-validated objects" ^ Step(CreateNonValidatedObjects) ^
  "Listing the ${contacts} should list the nonvalidated contact" ! containsNonValidated ^
  "The edit link for ${contacts} should be valid" ! editLink ^
  "Listing the ${extents} should list the nonvalidated extent" ! containsNonValidated ^
  "The edit link for ${extents} should be valid" ! editLink ^
  "Listing the ${formats} should list the nonvalidated format" ! containsNonValidated ^
  "The edit link for ${formats} should be valid" ! editLink ^
  "Listing the ${keywords} should list the nonvalidated keywords" ! containsNonValidated ^
  "The edit link for ${keywords} should be valid" ! editLink ^
                                                    Step(tearDown)

  // reuse the add reusable objects code from the other specs
  val contactSpec = new WP3.AddSharedContactsSpec()
  val formatSpec = new WP3.AddSharedFormatSpec()
  val extentSpec = new WP3.AddSharedExtentsSpec()
  val keywordSpec = new WP3.AddSharedKeywordsSpec()

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
     keywordSpec.keywordAdd("deValue" + uuid)()
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

  val containsNonValidated = (s: String) => {
    val listing = list(s)
    val uuid = extract1(s) match {
      case "contacts" => contactSpec.uuid
      case "extents" => extentSpec.uuid
      case "formats" => formatSpec.uuid
      case "keywords" => keywordSpec.uuid
    }
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

}