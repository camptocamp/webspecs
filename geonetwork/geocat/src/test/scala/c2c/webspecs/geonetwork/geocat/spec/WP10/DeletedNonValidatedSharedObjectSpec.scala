package c2c.webspecs
package geonetwork
package geocat
package spec.WP10

import org.specs2.specification.Step
import edit.AddSites._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import c2c.webspecs.geonetwork.geocat.shared.DeleteSharedUser

@RunWith(classOf[JUnitRunner])
class DeletedNonValidatedSharedObjectSpec extends AbstractSharedObjectSpec { def is =
  "Delete NonValidated Shared Object Specification".title           ^ Step(setup) ^ 
  "This specification declares the behaviour of deleting a shared object"                   ^ 
      "First create several shared objects"                                                 ^ Step(CreateNonValidatedObjects) ^
      "Then import a metadata and add the shared objects to that Metadata"                  ^ Step(createMetadata) ^ bt ^
      ("Deleting the contact via the normal delete contact API " +
      		"will move the contact to deleted object table and will update the" +
      		"metadata with the new xlink")                                                  ! deleteContact ^  
	  ("Deleting the extent via the normal delete contact API " +
	        "will move the contact to deleted object table and will update the" +
	        "metadata with the new xlink")                                                  ! deleteExtent(false) ^  
      ("Deleting the format via the normal delete contact API " +
            "will move the contact to deleted object table and will update the" +
            "metadata with the new xlink")                                                  ! deleteFormat ^ 
      ("Deleting the keyword via the normal delete contact API " +
            "will move the contact to deleted object table and will update the" +
            "metadata with the new xlink")                                                  ! deleteKeyword(false) ^  
                                                                                              Step(tearDown)

  def deleteContact = {
    val userId = doFindSharedContact.get.id
    val deleteResponse = DeleteUser(userId).execute()
    val normalDeleteFails = deleteResponse must haveAResponseCode(500)

    val correctDeletionResponse = DeleteSharedUser(userId, false).execute()
    val sharedUserDeleteHas200Response = correctDeletionResponse must haveA200ResponseCode

    normalDeleteFails and sharedUserDeleteHas200Response and validateCorrectRejection(correctDeletionResponse.value.id, contact)
  }
  def deleteExtent(validated: Boolean) = (s: String) => {
    val extentId = doFindSharedExtent.get.id
    val extentType = if (validated) Extents.Validated else Extents.NonValidated
    val deletionResponse = DeleteExtent(extentType, extentId, false).execute()
    val deleteHas200Response = deletionResponse must haveA200ResponseCode

    deleteHas200Response and validateCorrectRejection(deletionResponse.value.id, extent)
  }

  def deleteFormat = {
    val formatId = doFindSharedFormat.get.id
    val deletionResponse = DeleteFormat(false).execute(formatId.toInt)
    val deleteHas200Response = deletionResponse must haveA200ResponseCode

    deleteHas200Response and validateCorrectRejection(deletionResponse.value.id, distributionFormat)
  }
  def deleteKeyword(validated: Boolean) = (s:String) => {
    val keywordId = keywordHref.split("&").find(_ startsWith "id=").get.decode.split("#")(1)
    val thesaurus = if (validated) GeocatConstants.GEOCAT_THESAURUS else GeocatConstants.NON_VALIDATED_THESAURUS
    val deletionResponse = DeleteKeyword(thesaurus, GeocatConstants.KEYWORD_NAMESPACE, keywordId, false).execute()
    
    val deleteHas200Response = deletionResponse must haveA200ResponseCode

    deleteHas200Response and validateCorrectRejection(deletionResponse.value.id, descriptiveKeywords)
  }

  def doFindSharedExtent = findSharedExtent
  def doFindSharedFormat = findSharedFormat
  def doFindSharedContact = findSharedContact
}