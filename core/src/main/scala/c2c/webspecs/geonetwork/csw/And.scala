package c2c.webspecs.geonetwork.csw

case class And(val filters:OgcFilter*) extends OgcFilter {
	def xml = <ogc:And>{filters.map(_.xml)}</ogc:And>
	override def and(other:OgcFilter) = other match {
	  case otherAnd:And => And(filters ++ otherAnd.filters :_*)
	  case other => And(filters :+ other:_*)
	}
}