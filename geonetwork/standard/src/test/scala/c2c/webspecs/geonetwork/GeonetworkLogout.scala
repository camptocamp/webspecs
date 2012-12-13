package c2c.webspecs
package geonetwork


class GeonetworkLogout extends AbstractFormPostRequest[Any,XmlValue]("/srv/eng/xml.user.logout", XmlValueFactory) {
  override def request(in:Any, uriResolver:UriResolver) = {
    super.request(in, uriResolver)
  }
}

