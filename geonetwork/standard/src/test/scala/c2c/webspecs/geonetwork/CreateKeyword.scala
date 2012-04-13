package c2c.webspecs
package geonetwork

/**
 * Create a new Keyword and add it to the thesaurus indicated
 * @param id
 * @param namespace
 * @param thesaurus the thesaurus the word will be added to
 * @param words 2 letter lang code -> word
 */
case class CreateKeyword(namespace: String, id: String, thesaurus: String, words: (String, String)*)
  extends Request[Any, XmlValue] {
  def execute(in: Any)(implicit context: ExecutionContext, uriResolvers:UriResolver) = {
    
    val request = words.headOption.map {
      case (lang, word) => GetRequest("thesaurus.addelement", UpdateKeyword.params(namespace, id, thesaurus,lang,word): _*)
    }
    ((request getOrElse NoRequest) then UpdateKeyword(namespace, id, thesaurus, words.drop(1):_*)).execute()
  }
}