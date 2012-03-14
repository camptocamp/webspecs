package c2c.webspecs
package geonetwork
package csw

case class PropertyIsBetween(name:String,from:AnyVal, to:AnyVal) extends OgcFilter {
  override val xml =
    <ogc:PropertyIsBetween>
    <ogc:PropertyName>{name}</ogc:PropertyName>
    <ogc:LowerBoundary><ogc:Literal>{from}</ogc:Literal></ogc:LowerBoundary>
    <ogc:UpperBoundary><ogc:Literal>{to}</ogc:Literal></ogc:UpperBoundary>
  </ogc:PropertyIsBetween>
  override def cql = name+" BETWEEN "+from+" AND "+to
}
