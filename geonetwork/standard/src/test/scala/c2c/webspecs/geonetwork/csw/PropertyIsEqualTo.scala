package c2c.webspecs
package geonetwork
package csw

case class PropertyIsEqualTo(name:String,literal:String) extends OgcFilter {
  override val xml =
   <ogc:PropertyIsEqualTo>
    <ogc:PropertyName>{name}</ogc:PropertyName> <ogc:Literal>{literal}</ogc:Literal>
  </ogc:PropertyIsEqualTo>
  override def cql = name+" = '"+literal+"'"
  override def not = PropertyIsNotEqualTo(name,literal)
}
