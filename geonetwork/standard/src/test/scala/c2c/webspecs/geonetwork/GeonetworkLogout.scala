package c2c.webspecs
package geonetwork


class GeonetworkLogout extends AbstractFormPostRequest[Any,XmlValue]("/j_spring_security_logout", XmlValueFactory) {
  override def request(in:Any, uriResolver:UriResolver) = {
    super.request(in, uriResolver)
  }
}

