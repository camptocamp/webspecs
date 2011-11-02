package c2c.webspecs.geonetwork.csw

case class And(val filters:OgcFilter*) extends OgcFilter {
	override def xml = <ogc:And>{filters.map(_.xml)}</ogc:And>
	override def cql = filters.map(_.cql).mkString(" AND ")
    override def and(other:OgcFilter) = other match {
	  case otherAnd:And => And(filters ++ otherAnd.filters :_*)
	  case other => And(filters :+ other:_*)
	}
	override def toString = filters mkString " and "
}