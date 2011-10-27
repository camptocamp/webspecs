package c2c.webspecs
package geonetwork
package geocat

import c2c.webspecs.Request

/**
 * Resolve an xlink as a "normal" user
 */
object ResolveXLink extends Request[String, XmlValue] {
  def execute(in: String)(implicit context: ExecutionContext) = {
    val login = context.currentUser.map(_._1) getOrElse NoRequest
    (GetRequest("user.logout") then GetRequest(in) startTrackingThen login).execute()._1
  }
}