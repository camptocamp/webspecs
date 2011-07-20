package c2c.webspecs
package geonetwork

/**
 * Searches keywords using geonetwork APIs
 */
case class SearchKeywords(thesaurus:List[String] = Nil, lang:String = "*")
  extends AbstractGetRequest[String,List[KeywordRef]]("xml.search.keywords",KeywordRefListFactory,
    Seq[Param[String,String]](
        SP("pNewSearch", "true"),
        SP("pLanguage", lang),
        SP("pTypeSearch", "1"),
        IdP("pKeyword")
     ) ++ thesaurus.map{t => P("pThesauri" -> t)} :_*
  )
