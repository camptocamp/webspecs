package c2c.webspecs
package geonetwork
package spec.regions

import c2c.webspecs.geonetwork.regions._

trait AbstractRegionsSpec {
   protected val regions1 = Seq(
    Region(
      id = "http://geonetwork-opensource.org/regions#211",
      categoryId = "http://geonetwork-opensource.org/regions#country",
      label = LocalisedString(Map("eng" -> "Switzerland")),
      categoryLabel = LocalisedString(Map("eng" -> "Country", "ger" -> "Land")),
      bbox = Bbox(5.96701, 45.82944, 10.48821, 47.80666),
      hasGeom = false),
    Region(
      id = "http://geonetwork-opensource.org/regions#68",
      categoryId = "http://geonetwork-opensource.org/regions#country",
      label = LocalisedString(Map("eng" -> "France")),
      categoryLabel = LocalisedString(Map("eng" -> "Country")),
      bbox = Bbox(-5.79028, 41.36493, 9.56222, 51.09111),
      hasGeom = false))
   protected lazy val regions1Ids = regions1.map(_.id)
   protected val regions2 = Seq(
    Region(
      id = "http://geonetwork-opensource.org/regions#1220",
      categoryId = "http://geonetwork-opensource.org/regions#oceans",
      label = LocalisedString(Map("eng" -> "All fishing areas")),
      categoryLabel = LocalisedString(Map("eng" -> "ocean")),
      bbox = Bbox(11.11333, 55.33917, 24.16701, 69.0603),
      hasGeom = false))
   protected lazy val regions2Ids = regions2.map(_.id)
}