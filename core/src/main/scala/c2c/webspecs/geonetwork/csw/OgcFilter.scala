package c2c.webspecs.geonetwork.csw
import scala.xml.Node

trait OgcFilter {
	def and(other:OgcFilter) = And(this, other)
	
	def xml:Node
}