package c2c.webspecs
package geonetwork
package geocat

import shared._

class GeocatCreateUserDeleteMetadataLifeCycle(config: GeonetConfig) extends CreateUserDeleteMetadataLifeCycle(config) {
 override def deleteAllMetadata(implicit executionContext:ExecutionContext, uriResolver:UriResolver) = {
   super.deleteAllMetadata;
   SharedUserProfile.name // seed system with new profile
   val extents = SearchExtent(typeName = Extents.NonValidated :: Extents.Validated :: Nil).execute("*").value
   val keywords = SearchKeywords(GeocatConstants.NON_VALIDATED_THESAURUS :: GeocatConstants.GEOCAT_THESAURUS :: Nil).execute("*").value.toSet
   val formats = ListFormats.execute("").value
   val users = GeocatListUsers.execute("").value
   
   for (user <- users ) {
     if(user.validated) {
         GetRequest("validated.shared.user.remove", 'id -> user.idOption.get, 'testing -> true, 'forceDelete -> true).execute()
     } else {
         GetRequest("nonvalidated.shared.user.remove", 'id -> user.idOption.get, 'testing -> true, 'forceDelete -> true).execute()
     }
   }
   
   for (extent <- extents) {
     val typeName = if(extent.validated) Extents.Validated else Extents.NonValidated
     DeleteExtent(typeName, extent.id, true).execute()
   }
   
   for (keyword <- keywords ) DeleteKeyword(keyword, true).execute()
   for (format <- formats ) DeleteFormat(true).execute(format.id)
   
   val deletedObjs = ListDeletedSharedObjects.execute().value
   
   for (rejected <- deletedObjs) DeleteSharedObject(rejected.id).execute()
 }
}