package c2c.webspecs
package geonetwork
package geocat

import c2c.webspecs.Request

/**
 * Resolve an xlink as a "normal" user
 */
object ResolveXLink extends Request[String, XmlValue] {
  def execute(in: String)(implicit context: ExecutionContext, uriResolver:UriResolver) = {
    val login = context.currentUser.map(_._1) getOrElse NoRequest
    val result = (GeonetworkLogout then GetRequest(in)).execute()
    login.execute()
    result
  }
}