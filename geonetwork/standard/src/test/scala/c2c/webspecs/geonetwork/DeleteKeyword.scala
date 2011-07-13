package c2c.webspecs
package geonetwork

case class DeleteKeyword(thesaurus:String, namespace:String, code:String)
  extends AbstractGetRequest(
    "thesaurus.deleteelement",
    XmlValueFactory,
    SP("pThesaurus" -> thesaurus),
    SP("namespace" -> namespace),
    SP("code", code)
  )