package c2c.webspecs
package geonetwork

object UpdateKeyword {
  def params(namespace: String, id: String, thesaurus: String, lang: String, word: String) = Seq(
        "namespace" -> namespace,
      "oldid" -> id,
      "newid" -> id,
      "refType" -> "unknown",
      "ref" -> thesaurus,
      "lang" -> lang.toUpperCase(),
      "prefLab" -> word
  )
}
/**
 * Create a new Keyword and add it to the thesaurus indicated
 * @param id
 * @param namespace
 * @param thesaurus the thesaurus the word will be added to
 * @param words 2 letter lang code -> word
 */
case class UpdateKeyword(namespace: String, id: String, thesaurus: String, words: (String, String)*)
  extends Request[Any, XmlValue] {
  def execute(in: Any)(implicit context: ExecutionContext, uriResolver:UriResolver) = {
    
    val request = words.foldLeft(NoRequest:Request[Any,XmlValue]) {
      case (accRequest,(lang, word)) =>
        val allParams = UpdateKeyword.params(namespace, id, thesaurus,lang,word) ++ Seq(
            "oldid" -> id,
            "definition" -> "")
        accRequest then GetRequest("thesaurus.updateelement", allParams: _*)
    }
    request.execute()
  }
}