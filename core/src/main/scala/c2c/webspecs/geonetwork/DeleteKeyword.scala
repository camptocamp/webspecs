package c2c.webspecs
package geonetwork

import java.net.URLEncoder
import geocat.DeletedSharedObjectIdFactory

object DeleteKeyword {
  def apply(keywordRef:KeywordRef, forceDelete:Boolean):DeleteKeyword = DeleteKeyword(keywordRef.thesaurus, keywordRef.namespace, keywordRef.code, forceDelete)
}
case class DeleteKeyword(thesaurus:String, namespace:String, code:String, forceDelete:Boolean)
  extends AbstractGetRequest(
    "thesaurus.deleteelement",
    DeletedSharedObjectIdFactory,
    SP("pThesaurus" -> thesaurus),
    SP("namespace" -> namespace.encode),
    SP("forceDelete" -> forceDelete),
    SP("testing" -> true),
    SP("id"-> code.encode)
  )