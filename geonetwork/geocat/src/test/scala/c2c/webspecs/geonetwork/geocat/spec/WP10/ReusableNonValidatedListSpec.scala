package c2c.webspecs
package geonetwork
package geocat
package spec
package WP10

import shared._
import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ReusableNonValidatedListSpec extends AbstractSharedObjectSpec  { def is =
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
      case "keywords" => "thesaurus.admin"
    }
    listing.value.map(_.url.get) must =~(url).forall
  }

  val localURL = (s: String) => {
    val listing = list(s)

    listing.value.map(_.url.get) must startWith("local://").forall
  }


  val findReferenced = (s: String) => {
    val listing = list(s).value
    val specificUuid = lookupUUID(s)

    val obj = listing find { _.description contains specificUuid.toString } get

    val referenced = ListReferencingMetadata(obj.id, obj.objType).execute().value
    val correctId = referenced.map(_.mdId) must contain(createMetadata.id.toInt)
    val correctTitle = referenced.map(_.title) must contain(uuid.toString).atLeastOnce
    correctId and correctTitle
  }
}