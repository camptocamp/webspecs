package c2c.webspecs.geonetwork.csw
import javax.naming.OperationNotSupportedException

case class Within(areacode:String) extends OgcFilter{
  override def xml = <ogc:Within><ogc:PropertyName>ows:BoundingBox</ogc:PropertyName><gml:MultiPolygon xmlns:gml="http://www.opengis.net/gml" gml:id={"region:"+areacode}/></ogc:Within>
  override def cql = throw new OperationNotSupportedException("Within not supported")
}
case class Contains(areacode:String) extends OgcFilter{
  override def xml = <ogc:Contains><ogc:PropertyName>ows:BoundingBox</ogc:PropertyName><gml:MultiPolygon xmlns:gml="http://www.opengis.net/gml" gml:id={"region:"+areacode}/></ogc:Contains>
  override def cql = throw new OperationNotSupportedException("Contains not supported")
}
