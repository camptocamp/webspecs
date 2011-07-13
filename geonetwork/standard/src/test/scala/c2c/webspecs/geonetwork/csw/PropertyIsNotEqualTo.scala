package c2c.webspecs
package geonetwork
package csw

case class PropertyIsNotEqualTo(name:String,literal:String) {
  val xml =
   <ogc:PropertyIsNotEqualTo>
    <ogc:PropertyName>{name}</ogc:PropertyName> <ogc:Literal>{literal}</ogc:Literal>
  </ogc:PropertyIsNotEqualTo>
}
