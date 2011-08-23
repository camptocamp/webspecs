package c2c.webspecs
package geonetwork
package geocat
package spec.WP10

import org.specs2.specification.Step
import edit.AddSites._

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
	        "metadata with the new xlink")                                                  ! deleteExtent ^  
      ("Deleting the format via the normal delete contact API " +
            "will move the contact to deleted object table and will update the" +
            "metadata with the new xlink")                                                  ! deleteFormat ^  
      ("Deleting the keyword via the normal delete contact API " +
            "will move the contact to deleted object table and will update the" +
            "metadata with the new xlink")                                                  ! deleteKeyword ^  
                                                                                              Step(tearDown)
 
    def deleteContact = {
        val userId = findSharedContact.get.id
        val deleteResponse = DeleteUser(userId)()
        val normalDeleteFails = deleteResponse must haveAResponseCode(500)
        
        val correctDeletionResponse = DeleteSharedUser(userId)()
        val sharedUserDeleteHas200Response = correctDeletionResponse must haveA200ResponseCode 
        
        normalDeleteFails and sharedUserDeleteHas200Response and validateCorrectRejection(correctDeletionResponse.value.id, contact)
    }
    def deleteExtent = {
        val extentId = findSharedExtent.get.id
        val deletionResponse = DeleteExtent(Extents.NonValidated, extentId)()
        val deleteHas200Response = deletionResponse must haveA200ResponseCode 
        
        deleteHas200Response and validateCorrectRejection(deletionResponse.value.id, extent)
    }
  
    def deleteFormat = {
        val formatId = findSharedFormat.get.id
        val deletionResponse = DeleteFormat(formatId.toInt)
        val deleteHas200Response = deletionResponse must haveA200ResponseCode 
        
        deleteHas200Response and validateCorrectRejection(deletionResponse.value.id, extent)
    }  
    def deleteKeyword = pending  

}