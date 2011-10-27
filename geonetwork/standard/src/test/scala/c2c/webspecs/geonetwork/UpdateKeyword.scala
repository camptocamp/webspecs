package c2c.webspecs
package geonetwork
import java.net.URLEncoder

object UpdateKeyword {
  def params(namespace: String, id: String, thesaurus: String, lang: String, word: String) = Seq(
        "namespace" -> namespace.encode,
      "oldid" -> id.encode,
      "newid" -> id.encode,
      "refType" -> "unknown",
      "ref" -> thesaurus,
      "lang" -> lang.toUpperCase(),
      "prefLab" -> word.encode)
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
  def execute(in: Any)(implicit context: ExecutionContext) = {
    
    val request = words.foldLeft(NoRequest:Request[Any,XmlValue]) {
      case (accRequest,(lang, word)) =>
        val allParams = UpdateKeyword.params(namespace, id, thesaurus,lang,word) ++ Seq(
            "oldid" -> id.encode, 
            "definition" -> "")
        accRequest then GetRequest("thesaurus.updateelement", allParams: _*)
    }
    request.execute()
  }
}