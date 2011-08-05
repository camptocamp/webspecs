package c2c.webspecs.geonetwork.csw

case class And(filters:OgcFilter*) extends OgcFilter {
	def xml = <ogc:And>{filters.map(_.xml)}</ogc:And>
}