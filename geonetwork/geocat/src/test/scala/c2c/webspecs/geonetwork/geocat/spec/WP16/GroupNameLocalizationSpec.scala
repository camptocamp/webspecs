package c2c.webspecs
package geonetwork
package geocat
package spec.WP16

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._

@RunWith(classOf[JUnitRunner])
class GroupNameLocalizationSpec extends GeocatSpecification(UserProfiles.Admin) {  def is =
	"Group Localization".title 															 ^ Step(setup) ^
	"A new group should have 3 char localizations"										 ! createGroup ^
																						   Step(tearDown)
  def createGroup = {
    config.adminLogin then CreateGroup(Group("newGroup"+uuid,"newGroup des")) execute ()
    
    val response = XmlPostRequest("xml.info", <request><type>groups</type></request>).execute()
    val groupLabels = response.value.getXml \\ "group" \\ "label" \ "_" map (_.label)
    (response must haveA200ResponseCode) and
      (groupLabels must have((_: String).size == 3))
  }
}
