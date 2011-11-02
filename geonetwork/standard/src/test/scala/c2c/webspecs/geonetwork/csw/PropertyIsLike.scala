package c2c.webspecs
package geonetwork
package csw

case class PropertyIsLike(name:String,literal:String) extends OgcFilter {
  override val xml =
   <ogc:PropertyIsLike wildCard="*" singleChar="." escape="!">
    <ogc:PropertyName>{name}</ogc:PropertyName> <ogc:Literal>{literal}</ogc:Literal>
  </ogc:PropertyIsLike>
  override def cql = name+" LIKE '"+literal+"'"
}