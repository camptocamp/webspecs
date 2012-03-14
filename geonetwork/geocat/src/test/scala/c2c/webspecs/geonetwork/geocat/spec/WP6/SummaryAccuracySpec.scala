package c2c.webspecs
package geonetwork
package geocat
package spec.WP6

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.csw._

@RunWith(classOf[JUnitRunner])
class SummaryAccuracySpec extends geonetwork.spec.search.SummaryAccuracySpec {
  override def languagesToTest = Seq("eng", "fra", "deu")
}