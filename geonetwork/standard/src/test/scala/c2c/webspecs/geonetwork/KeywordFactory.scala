package c2c.webspecs
package geonetwork

class KeywordFactory(thesaurus:String) extends ValueFactory[String,IsoKeyword] {
  def createValue[A <: String, B >: IsoKeyword](
      request: Request[A, B],
      uri: String,
      rawValue: BasicHttpValue,
      executionContext: ExecutionContext, 
      uriResolver:UriResolver): IsoKeyword = {
    rawValue.toXmlValue.withXml{
      keywordXml =>
        val translationNodes = keywordXml \\ "LocalisedCharacterString"
        val translations = translationNodes.toSeq.map{n =>
          ((n \\ "@locale" text).drop(1).toUpperCase, n.text)
        }
        IsoKeyword(uri,thesaurus, Map(translations:_*))
    }
  }
}