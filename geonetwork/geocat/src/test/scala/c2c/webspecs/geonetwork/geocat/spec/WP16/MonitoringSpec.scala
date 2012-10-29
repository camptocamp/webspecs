package c2c.webspecs
package geonetwork
package geocat
package spec.WP16

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step

import c2c.webspecs.geonetwork.UserProfiles


/**
 * We have migrated to metrics so we need to update this to check metrics instead
 */
@RunWith(classOf[JUnitRunner]) 
class MonitoringSpec extends geonetwork.spec.misc.MonitoringSpec with GeocatSpecification {
  override def mdToImport = "/geocat/data/comprehensive-iso19139che.xml"
}
