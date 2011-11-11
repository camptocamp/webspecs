package c2c.webspecs
package geonetwork


object GeonetworkLogout extends AbstractGetRequest[Any,XmlValue]("user.logout", XmlValueFactory) {
  override def request(in:Any, uriResolver:UriResolver) = {
    super.request(in, uriResolver)
  }
}