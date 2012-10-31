package c2c.webspecs
package geonetwork
package geocat
package spec.WP9

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml._
import org.specs2.specification.Fragments

@RunWith(classOf[JUnitRunner])
class MetadataShowSpec extends c2c.webspecs.geonetwork.spec.get.MetadataShowSpec with GeocatSpecification {

  override def extraTests: Fragments = "metadata.show should not have xlinks tags" ! noXLinks
  override def importStyleSheet = GeocatConstants.GM03_2_TO_CHE_STYLESHEET
  override def xmlFile = "/geocat/data/gm03_2_shows_xlinks_when_imported.xml"

  def noXLinks = {
    val xlinkParents = importSampleFile.en \\ "_" filter { n => n.child.exists(n => n.isInstanceOf[Text] && (n.text.trim startsWith "xlink:")) }
    xlinkParents must beEmpty
  }

}