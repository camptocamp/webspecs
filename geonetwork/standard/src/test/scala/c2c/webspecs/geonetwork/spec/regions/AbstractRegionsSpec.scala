package c2c.webspecs
package geonetwork
package spec.regions

import c2c.webspecs.geonetwork.regions._

trait AbstractRegionsSpec {
   protected val regions1 = Seq(
    Region(
      id = "211",
      categoryId = "country",
      label = LocalisedString(Map("eng" -> "Switzerland")),
      categoryLabel = LocalisedString(Map("eng" -> "country")),
      bbox = Bbox(5.96701, 45.82944, 10.48821, 47.80666),
      hasGeom = false),
    Region(
      id = "210",
      categoryId = "country",
      label = LocalisedString(Map("eng" -> "Sweden")),
      categoryLabel = LocalisedString(Map("eng" -> "country")),
      bbox = Bbox(11.11333, 55.33917, 24.16701, 69.0603),
      hasGeom = false))
   protected lazy val regions1Ids = regions1.map(_.id)
   protected val regions2 = Seq(
    Region(
      id = "1220",
      categoryId = "oceans",
      label = LocalisedString(Map("eng" -> "Sweden")),
      categoryLabel = LocalisedString(Map("eng" -> "country")),
      bbox = Bbox(11.11333, 55.33917, 24.16701, 69.0603),
      hasGeom = false))
   protected lazy val regions2Ids = regions2.map(_.id)
}