package c2c.webspecs
package geonetwork
package geocat
package spec.WP16

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.specs2.matcher.Matcher

@RunWith(classOf[JUnitRunner])
class XmlInfoServiceLocalisationSpec extends GeocatSpecification(UserProfiles.Admin) {
  def is =
    "Xml.info localization".title ^ Step(setup) ^
      "All categories should have 3 char locale codes" ! categoryLocalization ^
      "All groups should have 3 char locale codes" ! groupsLocalization ^
      Step(tearDown)

  def allHaveSize3: Matcher[Traversable[String]] = have((_: String).size == 3)
  def categoryLocalization = {
    config.adminLogin.execute()
    val response = XmlPostRequest("xml.info", <request><type>categories</type></request>).execute()
    val categoryLabels = response.value.getXml \\ "category" \\ "label" \ "_" map (_.label)
    (response must haveA200ResponseCode) and
      (categoryLabels must allHaveSize3)
  }
  def groupsLocalization = {
    config.adminLogin.execute()
    val response = XmlPostRequest("xml.info", <request><type>groups</type></request>).execute()
    val groupLabels = response.value.getXml \\ "group" \\ "label" \ "_" map (_.label)
    (response must haveA200ResponseCode) and
      (groupLabels must allHaveSize3)
  }
}
