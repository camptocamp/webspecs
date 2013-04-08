package c2c.webspecs
package geonetwork
package geocat
package shared

import c2c.webspecs.geonetwork.geocat.shared.SharedObjectTypes._

object ListValidatedExtents
  extends AbstractGetRequest[Any, List[SharedObject]](
    "extent.search.paging!",
    SelfValueFactory[Any, List[SharedObject]],
    SP('pattern -> '*'),
    SP('property -> 'desc),
    SP('numResults -> 1000),
    SP('typename -> Extents.Validated.name)) 
  with BasicValueFactory[List[SharedObject]] {

  def createValue(rawValue: BasicHttpValue): List[SharedObject] = {
    val extents = rawValue.toXmlValue.getXml \ "response" \ "wfs" \ "featureType" \ "feature"

    extents.toList map { extent =>
        val id = (extent \ "@id").text.trim
        val url = (extent \ "href").headOption.map(_.text.trim)
        val description = (extent \ "desc" \ "_").map{p => p.text.trim()}.filter(_.nonEmpty).headOption getOrElse ""
        val objType = SharedObjectTypes.extents

        SharedObject(id, url, description, objType)
    }
  }
}
