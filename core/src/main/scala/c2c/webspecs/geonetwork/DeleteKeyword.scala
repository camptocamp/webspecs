package c2c.webspecs
package geonetwork

import java.net.URLEncoder

object DeleteKeyword {
  def apply(keywordRef:KeywordRef):DeleteKeyword = DeleteKeyword(keywordRef.thesaurus, keywordRef.namespace, keywordRef.code)
}
case class DeleteKeyword(thesaurus:String, namespace:String, code:String)
  extends AbstractGetRequest(
    "thesaurus.deleteelement",
    XmlValueFactory,
    SP("pThesaurus" -> thesaurus),
    SP("namespace" -> URLEncoder.encode(namespace,"UTF-8")),
    SP("id"-> URLEncoder.encode(code,"UTF-8"))
  )