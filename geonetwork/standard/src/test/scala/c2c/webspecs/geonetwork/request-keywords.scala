package c2c.webspecs
package geonetwork

import c2c.webspecs.AbstractGetRequest

/**
 * Searches keywords using geonetwork APIs
 */
case class SearchKeywords(thesaurus:List[String] = Nil, lang:String = "*")
  extends AbstractGetRequest[String,List[KeywordRef]]("xml.search.keywords",new KeywordRefListFactory(thesaurus),
    P("pNewSearch", "true"),
    P("pLanguage", lang),
    P("pTypeSearch", "1"),
    P("pThesauri", thesaurus.mkString(",")),
    IdP("pKeyword")
  )

/**
 * Represents a Geonetwork keyword
 */
case class KeywordRef(id:String,value:String,definition:String,uri:String,thesaurus:List[String])
case class IsoKeyword(uri:String,thesaurus:String,labels:Map[String,String])

/**
 * Convert xml.search.keywords results to a list of keywords
 */
class KeywordRefListFactory(thesaurus:List[String]) extends BasicValueFactory[List[KeywordRef]]{
  def createValue(rawValue: BasicHttpValue): List[KeywordRef] = {
    rawValue.toXmlValue.withXml{xml =>
      (xml \\ "keyword").toList map {
        wordElem =>
          val id = wordElem \\ "id" text
          val value = wordElem \\ "value" text
          val definition = wordElem \\ "definition" text
          val uri = wordElem \\ "uri" text

          KeywordRef(id,value,definition,uri,thesaurus)
      }
    }
  }
}

class KeywordFactory(thesaurus:String) extends ValueFactory[String,IsoKeyword] {
  def createValue[A <: String, B >: IsoKeyword](
      request: Request[A, B],
      uri: String,
      rawValue: BasicHttpValue,
      executionContext: ExecutionContext): IsoKeyword = {
    rawValue.toXmlValue.withXml{
      keywordXml =>
        val translationNodes = keywordXml \\ "LocalisedCharacterString"
        val translations = translationNodes.toSeq.map{n =>
          ((n \\ "@locale" text).drop(1), n.text)
        }
        IsoKeyword(uri,thesaurus, Map(translations:_*))
    }
  }
}
case class GetIsoKeyword(thesaurus:String, locales:List[String])
  extends AbstractGetRequest[String,IsoKeyword](
    "che.keyword.get",
    new KeywordFactory(thesaurus),
    SP("thesaurus",thesaurus),
    IdP("id"),
    SP("locales", locales mkString ",")
  )
