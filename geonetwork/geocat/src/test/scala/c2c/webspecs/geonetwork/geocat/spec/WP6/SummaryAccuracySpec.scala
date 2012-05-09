package c2c.webspecs
package geonetwork
package geocat
package spec.WP6

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SummaryAccuracySpec extends geonetwork.spec.search.SummaryAccuracySpec {
  override def languagesToTest = Seq("eng", "fra", "deu", "fre", "ger")
  override def addData = success
}