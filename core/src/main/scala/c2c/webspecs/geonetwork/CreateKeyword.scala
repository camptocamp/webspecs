package c2c.webspecs
package geonetwork
import java.net.URLEncoder

/**
 * Create a new Keyword and add it to the thesaurus indicated
 * @param id
 * @param namespace
 * @param thesaurus the thesaurus the word will be added to
 * @param words 2 letter lang code -> word
 */
case class CreateKeyword(namespace: String, id: String, thesaurus: String, words: (String, String)*)
  extends Request[Any, Null] {
  def apply(in: Any)(implicit context: ExecutionContext) = {
    words.foreach {
      case (lang, word) =>
        GetRequest("thesaurus.addelement",
          "namespace" -> URLEncoder.encode(namespace, "UTF-8"),
          "newid" -> URLEncoder.encode(id,"UTF-8"),
          "refType" -> "unknown",
          "ref" -> thesaurus,
          "lang" -> lang,
          "prefLab" -> URLEncoder.encode(word,"UTF-8"))()
    }
    EmptyResponse
  }
}