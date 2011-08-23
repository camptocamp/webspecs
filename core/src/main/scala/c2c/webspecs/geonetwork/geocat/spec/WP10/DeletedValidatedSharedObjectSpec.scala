package c2c.webspecs
package geonetwork
package geocat
package spec.WP10

import org.specs2.specification.Step

class DeletedValidatedSharedObjectSpec extends AbstractSharedObjectSpec { def is =
  "Delete Validated Shared Object Specification".title           ^ Step(setup) ^ 
  "This specification declares the behaviour of deleting a shared object"                   ^ 
      "First create several shared objects"                                                 ^ Step(CreateNonValidatedObjects) ^
      "Validated all new shared objects"                                                    ^ Step(CreateNonValidatedObjects) ^
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
 
    def deleteContact = pending  
    def deleteExtent = pending  
    def deleteFormat = pending  
    def deleteKeyword = pending  

}