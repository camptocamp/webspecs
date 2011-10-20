package c2c.webspecs.geonetwork.csw

import scala.xml.Node

case class Not(filter:OgcFilter) extends OgcFilter {

  def xml(): Node = <ogc:Not>filter</ogc:Not>
    
  override def not = filter


}