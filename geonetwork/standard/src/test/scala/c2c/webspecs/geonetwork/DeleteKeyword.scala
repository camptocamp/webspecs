package c2c.webspecs
package geonetwork

import java.net.URLEncoder

object DeleteKeyword {
  def apply(keywordRef:KeywordRef, forceDelete:Boolean):DeleteKeyword = DeleteKeyword(keywordRef.thesaurus, keywordRef.namespace, keywordRef.code, forceDelete)
}
object DeletedObjectIdFactory extends BasicValueFactory[IdValue] {
  def createValue(rawValue: BasicHttpValue): IdValue = (rawValue.toXmlValue.getXml \\ "id" map {n => IdValue(n.text,rawValue)}).headOption.getOrElse(IdValue(null, rawValue))
}
case class DeleteKeyword(thesaurus:String, namespace:String, code:String, forceDelete:Boolean) 
	extends AbstractGetRequest(
    "thesaurus.deleteelement",
    Config.loadStrategy[BasicValueFactory[IdValue]]("deletedObjectIdFactory").fold(_ => DeletedObjectIdFactory, f => f.newInstance()),
    SP("pThesaurus" -> thesaurus),
    SP("namespace" -> namespace.encode),
    SP("forceDelete" -> forceDelete),
    SP("testing" -> true),
    SP("id"-> code.encode)
  )