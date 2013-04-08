package c2c.webspecs
package geonetwork
package geocat
package shared

import c2c.webspecs.geonetwork.geocat.shared.SharedObjectTypes._

object ListValidatedKeywords
  extends AbstractGetRequest[Any, List[SharedObject]](
    "xml.search.keywords",
    SelfValueFactory[Any, List[SharedObject]],
    SP('pNewSearch -> true),
    SP('pTypeSearch -> 1),
    SP('pThesauri -> "local._none_.geocat.ch"),
    SP('pMode -> 'searchBox),
    SP('maxResults -> 1000),
    SP('pLanguage -> '*'),
    SP('pKeyword -> '*'))
  with BasicValueFactory[List[SharedObject]] {

  def createValue(rawValue: BasicHttpValue): List[SharedObject] = {
    val keywords = rawValue.toXmlValue.getXml \ "descKeys" \ "keyword"

    keywords.toList map { kw =>
        val id = (kw \ "id").text.trim
        val url = Some(s"local://che.keyword.get?thesaurus=local._none_.geocat.ch&id="+id+"&locales=fr,en,de,it")
        val description = (kw \ "values" \ "_").map{p => p.text.trim()}.filter(_.nonEmpty).headOption getOrElse ""
        val objType = SharedObjectTypes.keywords

        SharedObject(id, url, description, objType)
    }
  }
}
