package c2c.webspecs
package geonetwork
package geocat
package spec.WP16

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner


@RunWith(classOf[JUnitRunner]) 
class RegisterXslSpec extends geonetwork.spec.formatter.RegisterFormatterSpec {
  override def xmlFile = "/geocat/data/metadata.iso19139.che.xml"
}
