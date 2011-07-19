package c2c.webspecs
package geonetwork
package geocat

import scala.xml.NodeSeq
import UserProfiles._

abstract class GeocatSpecification(userProfile: UserProfile = Editor) extends GeonetworkSpecification(userProfile) {
	override def extraTeardown(teardownContext:ExecutionContext):Unit = {
	  super.extraTeardown(teardownContext)
	  //ListUsers(None).
	}
}