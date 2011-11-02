package c2c.webspecs
package geonetwork
package csw

case class PropertyIsNotEqualTo(name:String,literal:String) extends OgcFilter {
  override val xml =
   <ogc:PropertyIsNotEqualTo>
    <ogc:PropertyName>{name}</ogc:PropertyName> <ogc:Literal>{literal}</ogc:Literal>
  </ogc:PropertyIsNotEqualTo>
  override def cql = name+" != '"+literal+"'"
    
  override def not = PropertyIsEqualTo(name,literal)
}
