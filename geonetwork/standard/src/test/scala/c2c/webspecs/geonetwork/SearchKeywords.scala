package c2c.webspecs
package geonetwork

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
