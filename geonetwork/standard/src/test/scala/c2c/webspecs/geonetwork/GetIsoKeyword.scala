package c2c.webspecs
package geonetwork

/**
 * Requests to get a IsoKeyword object
 * @thesaurus the thesaurus the keyword is in
 * @locales the locales to load (if they exist)
 */
case class GetIsoKeyword(thesaurus:String, locales:List[String])
  extends AbstractGetRequest[String,IsoKeyword](
    "che.keyword.get",
    new KeywordFactory(thesaurus),
    SP("thesaurus",thesaurus),
    InP("id", (id:String) => id),
    SP("locales", locales mkString ",")
  )
