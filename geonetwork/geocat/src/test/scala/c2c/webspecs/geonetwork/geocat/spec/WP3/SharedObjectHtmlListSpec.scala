package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AccessSharedObjectHtmlListSpecExtentsSpec extends GeocatSpecification { def is =
  "Verify that xlinks received by the various search services work" ^ Step(setup) ^
  "Search Extents and verify all extends have expected URL form" ! expectedExtentURIs ^
  "Search Contacts and verify all extends have expected URL form" ! expectedContactsURIs ^
  "Search Formats and verify all extends have expected URL form" ! expectedFormatsURIs ^
                                                                   Step(tearDown)

  val formatFixture = GeocatFixture.format
  val nonValidatedUserFixture = GeocatFixture.sharedUser(false)
  val validatedUserFixture = GeocatFixture.sharedUser(true)
  
  override lazy val fixtures = List(
    formatFixture,
    nonValidatedUserFixture,
    validatedUserFixture
  )
  
  def expectedExtentURIs = {
    val response = 
      GetRequest("extent.search.list", 
          'numResults -> 25,
          'property -> 'desc,
          'method -> 'loose,
          'format -> 'gmd_bbox,
          'pattern -> 'bern).execute()
          
     val hrefs = response.value.getXml \\ "li" \ "href" map (_.text)

     (hrefs must not beEmpty) and 
         (hrefs must =~("""id=\d+""").forall) and
         (hrefs must =~("""wfs=\w+""").forall) and
         (hrefs must =~("""typename=\w+:\w+""").forall) and
         (hrefs must =~("""format=GMD_BBOX""").forall) and
         (hrefs must beMatching("""^local://xml.extent.get\?.+""").forall)
  }
  def expectedContactsURIs = {
    val response = 
      GetRequest("shared.user.list", 
          'sortByValidated -> true,
          'name -> validatedUserFixture.name).execute()
          
     val hrefs = response.value.getXml \\ "li" \ "href" map (_.text)
     val valid = response.value.getXml \\ "li" \ "valid"  map (_.text.toBoolean)
     
     (hrefs must not beEmpty) and 
         (hrefs must beMatching("""^local://xml.user.get\?id=\d+""").forall) and
         (valid must beTrue.atLeastOnce) and
         (valid must beFalse.atLeastOnce)
  }
  def expectedFormatsURIs = {
    
    val response = 
      GetRequest("xml.format.list", 
          'order -> 'validated,
          'name-> formatFixture.name).execute()
          
     val hrefs = response.value.getXml \\ "li" \ "href" map (_.text)
     val valid = response.value.getXml \\ "li" \ "valid"  map (_.text.toBoolean)
     
     (hrefs must not beEmpty) and 
         (hrefs must beMatching("""^local://xml.format.get\?id=\d+""").forall) and
         (valid must not beEmpty)
  }

}