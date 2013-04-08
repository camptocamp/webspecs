package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import c2c.webspecs.geonetwork.geocat.GeocatSpecification
import org.specs2.execute.Result
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.ImportStyleSheets
import c2c.webspecs.geonetwork.geocat.shared._

@RunWith(classOf[JUnitRunner])
class DeleteValidatedSharedObjectSpec extends GeocatSpecification {
  def is = {
    "Import a metadata" ^ sequential ^ Step(setup) ^ Step(config.adminLogin.assertPassed()) ^ Step(importMetadata) ^ endp ^
      "Validate all shared objects" ^ Step(validateShared) ^ endp ^
      "All shared objects should now be validated" ! allValidated ^
      "All xlinks should be flagged as validated" ! xlinksValidated ^
      "Publish metadata" ^ Step(publishMetadata) ^ endp ^
      "List Referencing metadata of shared contacts should list metadata" ! contactReferencingMetadata ^
      "List Referencing metadata of shared formats should list metadata" ! formatReferencingMetadata ^
      "List Referencing metadata of shared keywords should list metadata" ! keywordReferencingMetadata ^
      "List Referencing metadata of shared extents should list metadata" ! extentReferencingMetadata ^
      "Delete contact" ! deleteContact ^
      "Delete format" ! deleteFormat ^
      "Delete Keyword" ! deleteKeyword ^
      "Delete Extent" ! deleteExtent ^
      Step(tearDown)
  }

  var id:Id = _
  def importMetadata = {
    id = importMd(1, "/geocat/data/comprehensive-new-extent-iso19139che.xml", uuid.toString(), ImportStyleSheets.NONE).head
  }
  
  def validateShared = {
    var validate = (it:SharedObject) => ValidateSharedObject(it).execute()
    ListNonValidatedContacts.execute().value.foreach (validate)
    ListNonValidatedFormats.execute().value.foreach (validate)
    ListNonValidatedKeywords.execute().value.foreach (validate)
    ListNonValidatedExtents.execute().value.foreach (validate)
  }

  def allValidated = {
    SharedObjectTypes.values.foldLeft (success: Result) { (r, objType) =>
      r and (ListNonValidated(objType).execute().value must beEmpty)
    }
  }
  
  def xlinksValidated = {
    val metadata = GetEditingMetadataXml.execute(id).value.getXml
    
    val xlinkRoles = (metadata \\ "_" filter {n => 
      (n @@ "xlink:role").exists(_.trim.nonEmpty)} flatMap {_ @@ "xlink:href"}).toSet

    xlinkRoles.filter(_.nonEmpty) must beEmpty
  }
  
  def publishMetadata = GetRequest(s"metadata.admin?id=$id&_1_0=on&_-1_0=on&_0_0=on").execute()

  private def referencingMd(request: Request[Any, List[SharedObject]]) = {
    val sharedObjs = request.execute().value
    val referenced = sharedObjs map { obj =>
      ListReferencingMetadata(obj.id, obj.objType).execute().value.size
    }
    
    (sharedObjs must not(beEmpty)) and (referenced must (be_>(0)).forall)

  }
  def contactReferencingMetadata = referencingMd(ListValidatedContacts)
  def formatReferencingMetadata = referencingMd(ListValidatedFormats)
  def keywordReferencingMetadata = referencingMd(ListValidatedKeywords)
  def extentReferencingMetadata = referencingMd(ListValidatedExtents)

  def deleteContact = {
    val contacts = ListValidatedContacts.execute().value
    contacts.foreach {user => DeleteSharedUser(user.id, false).assertPassed()}
    val metadata = GetEditingMetadataXml.execute(id).value.getXml
    val tagNames = List("parentResponsibleParty","citedResponsibleParty","pointOfContact","contact","userContactInfo","distributorContact")
    val nodes = (metadata \\ "_") filter {n => tagNames contains (n.label)}
    
    val nonDeleteNodes = nodes.filter(n => (n @@ "xlink:href") exists {n => n contains "local://xml.user.get"})

    nonDeleteNodes must beEmpty
  }
  def deleteFormat = {
    val formats = ListValidatedFormats.execute().value
    formats.foreach {format => DeleteFormat(false).assertPassed(format.id.toInt)}

    val metadata = GetEditingMetadataXml.execute(id).value.getXml
    val tagNames = List("distributionFormat","resourceFormat")
    val nodes = (metadata \\ "_") filter {n => tagNames contains (n.label)}
    
    val nonDeleteNodes = nodes.filter(n => (n @@ "xlink:href") exists {n => n contains "local://xml.format.get"})

    nonDeleteNodes must beEmpty
  }
  def deleteKeyword = {
    val keywords = ListValidatedKeywords.execute().value
    keywords.foreach {kw => 
      val ref = KeywordRef(kw.id, "en", "", "", "", "local._none_.geocat.ch")
      DeleteKeyword(ref, false).assertPassed()}

    val metadata = GetEditingMetadataXml.execute(id).value.getXml
    val tagNames = List("descriptiveKeyword")
    val nodes = (metadata \\ "_") filter {n => tagNames contains (n.label)}
    
    val nonDeleteNodes = nodes.filter(n => (n @@ "xlink:href") exists {n => n contains "local://che.keyword.get"})

    nonDeleteNodes must beEmpty
  }
  def deleteExtent = {
    val extents = ListValidatedExtents.execute().value
    extents.foreach {extent => DeleteExtent(Extents.Validated, extent.id, false).assertPassed()}

    val metadata = GetEditingMetadataXml.execute(id).value.getXml
    val tagNames = List("extent")
    val nodes = (metadata \\ "_") filter {n => tagNames contains (n.label)}
    
    val nonDeleteNodes = nodes.filter(n => (n @@ "xlink:href") exists {n => (n contains "local://xml.extent.get") && (n contains "gn:xlink")})
   
    nonDeleteNodes must beEmpty
  }

}