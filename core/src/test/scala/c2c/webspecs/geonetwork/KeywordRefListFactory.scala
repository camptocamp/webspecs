package c2c.webspecs
package geonetwork

/**
 * Convert xml.search.keywords results to a list of keywords
 */
object KeywordRefListFactory extends BasicValueFactory[List[KeywordRef]]{
  def createValue(rawValue: BasicHttpValue): List[KeywordRef] = {
    rawValue.toXmlValue.withXml{xml =>
      (xml \\ "keyword").toList map {
        wordElem =>
          val id = wordElem \\ "id" text
          val value = wordElem \\ "value" text
          val definition = wordElem \\ "definition" text
          val uri = wordElem \\ "uri" text
          val thesaurus= wordElem \\ "thesaurus" text

          KeywordRef(id,value,definition,uri,thesaurus)
      }
    }
  }
}
