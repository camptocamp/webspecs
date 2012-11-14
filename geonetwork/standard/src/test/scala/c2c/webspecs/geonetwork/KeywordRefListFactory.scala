package c2c.webspecs
package geonetwork

/**
 * Convert xml.search.keywords results to a list of keywords
 */
object KeywordRefListFactory extends BasicValueFactory[List[KeywordRef]]{
  def createValue(rawValue: BasicHttpValue): List[KeywordRef] = {
    (rawValue.toXmlValue.getXml \\ "keyword").toList flatMap {
        wordElem =>
          val multilingualKeyword = if(wordElem \ "definitions" nonEmpty) true else false
          val id = wordElem \ "id" text
          val defLang = wordElem \ "defaultLang" text 
          val values = if(wordElem \ "values" nonEmpty) {
            (wordElem \ "values" \ "value" filter {_.text.nonEmpty} map {v => ((v @@ "language").head, v.text)})
          } else {
            Seq(defLang -> (wordElem \ "value" text))
          }
          val definitions = if(wordElem \ "definitions" nonEmpty) {
            (wordElem \ "definitions" \ "definition" filter {_.text.nonEmpty} map {v => ((v @@ "language").head, v.text)}).toMap
          } else {
            Map(defLang -> (wordElem \ "definition" text))
          }
          val uri = wordElem \ "uri" text
          val thesaurus= wordElem \ "thesaurus" \ "key" text

          values.map {case (lang, value) => 
            KeywordRef(id,lang, value,definitions.get(lang) getOrElse "",uri,thesaurus)
          }
    }
  }
}
