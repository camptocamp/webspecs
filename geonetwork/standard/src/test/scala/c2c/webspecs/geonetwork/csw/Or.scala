package c2c.webspecs.geonetwork.csw

case class Or(filters:OgcFilter*) extends OgcFilter {
    def xml = <ogc:Or>{filters.map(_.xml)}</ogc:Or>
    def cql = filters.map(_.cql).mkString(" OR ")
    override def or(other:OgcFilter) = other match {
      case otherOr:Or => Or(filters ++ otherOr.filters :_*)
      case other => Or(filters :+ other:_*)
    }
    override def toString = filters mkString " or "

}