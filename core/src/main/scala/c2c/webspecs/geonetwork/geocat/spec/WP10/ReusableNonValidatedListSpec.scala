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
  "This specification tests the Server API for obtaining the list of non-validated objects" ^
  "First create some non-validated objects" ^ Step(setup) ^
  "First create some non-validated objects" ^ Step(CreateNonValidatedObjects) ^
  "Listing the ${contacts} should list the nonvalidated contact" ! containsNonValidated ^
  "The edit link for ${contacts} should be valid" ! editLink ^
  "Listing the ${extents} should list the nonvalidated extent" ! containsNonValidated ^
  "The edit link for ${extents} should be valid" ! editLink ^
  "Listing the ${format} should list the nonvalidated format" ! containsNonValidated ^
  "The edit link for ${formats} should be valid" ! editLink ^
  "Listing the ${keywords} should list the nonvalidated keywords" ! containsNonValidated ^
  "The edit link for ${keywords} should be valid" ! editLink ^
                                                    Step(tearDown)

  def CreateNonValidatedObjects = {
    config.adminLogin()
    new WP3.AddSharedContactsSpec().contactAdd()
    new WP3.AddSharedFormatSpec().formatAdd("1")
    new WP3.AddSharedExtentsSpec().extentAdd("${" + ExtentFormat.gmd_bbox + "}" + "${0}")
    new WP3.AddSharedKeywordsSpec().keywordAdd("deValue" + uuid)
  }

  def listRequest(s: String) = extract1(s) match {
    case "contacts" => ListNonValidatedContacts
    case "extents" => ListNonValidatedExtents
    case "formats" => ListNonValidatedFormats
    case "keywords" => ListNonValidatedKeywords
  }

  val containsNonValidated = (s: String) => {
    def request = listRequest(s)
    request().value find { _.description contains uuid } must beSome
  }

  val editLink = (s: String) => {
    def request = listRequest(s)
    val url = extract1(s) match {
      case "contacts" => "shared.user.edit"
      case "extents" => "extent.edit"
      case "formats" => "format.admin"
      case "keywords" => "format.admin"      
    }
    request().value.map(_.url.get) must =~(url).forall
  }

}