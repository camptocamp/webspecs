package c2c.webspecs.geonetwork

sealed trait SpatialRelation
case object Within extends SpatialRelation
case object Touches extends SpatialRelation
case object Encloses extends SpatialRelation
case object Overlaps extends SpatialRelation
case object OutsideOf extends SpatialRelation
case object Equals extends SpatialRelation
case object Intersection extends SpatialRelation
case object Crosses extends SpatialRelation
