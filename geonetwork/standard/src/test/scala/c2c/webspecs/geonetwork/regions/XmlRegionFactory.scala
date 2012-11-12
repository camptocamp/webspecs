package c2c.webspecs
package geonetwork
package regions

object XmlRegionFactory extends BasicValueFactory[List[Region]] {
  def createValue(rawValue:BasicHttpValue):List[Region] = {
    val xml = rawValue.toXmlValue.getXml
    val regions = for {
      region <- xml \ "region"
      id = (region \ "@id").text
      categoryId = (region \ "@categoryId").text
      hasGeom = (region \ "@hasGeom").text.toBoolean
      north = (region \ "north").text.toDouble
      south = (region \ "south").text.toDouble
      east = (region \ "east").text.toDouble
      west = (region \ "west").text.toDouble
    } yield {
      val label: LocalisedString = new LocalisedString((region \ "label" \ "_" map (e => e.label -> e.text)).toMap)
      val category: LocalisedString = new LocalisedString((region \ "categoryLabel" \ "_" map (e => e.label -> e.text)).toMap)
      Region(id, label, categoryId, category, Bbox(west,south, east, north),hasGeom)
    }
    
    regions.toList
  }
}
