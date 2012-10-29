package c2c.webspecs
package geonetwork
package geocat
package spec.WP5.csw.search

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._

@RunWith(classOf[JUnitRunner])
class NonSpatialCswSearchQuerySpec 
	extends c2c.webspecs.geonetwork.spec.csw.search.NonSpatialCswSearchQuerySpec 
	with SearchSpecification {
  override def extraTests = {
    super.extraTests ^
      "Searching for ${" + time + "-hyphen} in ${abstract} should return ${FR} imported md because the '-' is ignored during indexing" ! basicSearch(split = Some('-')) ^
      "Searching for ${" + time + "-hyphen} in ${" + anyFieldName + "} should return ${FR} imported md because the '-' is ignored during indexing" ! basicSearch(split = Some('-')) ^
      "Searching for ${" + time + " hyphen} in ${abstract} should return ${FR} imported md because the '-' is ignored during indexing" ! basicSearch(split = Some('-')) ^
      "Searching for ${" + time + "_underscore} in ${abstract} should return ${FR} imported md because the '_' is ignored during indexing" ! basicSearch(split = Some('_')) ^
      "Searching for ${" + time + "_underscore} in ${" + anyFieldName + "} should return ${FR} imported md because the '_' is ignored during indexing" ! basicSearch(split = Some('_')) ^
      "Searching for ${" + time + " underscore} in ${abstract} should return ${FR} imported md because the '_' is ignored during indexing" ! basicSearch(split = Some('_')) ^
      "Searching for ${" + time + "/forwardSlash} in ${abstract} should return ${FR} imported md because the '/' is ignored during indexing" ! basicSearch(split = Some('/')) ^
      "Searching for ${" + time + "/forwardSlash} in ${" + anyFieldName + "} should return ${FR} imported md because the '/' is ignored during indexing" ! basicSearch(split = Some('/')) ^
      "Searching for ${" + time + " forwardSlash} in ${abstract} should return ${FR} imported md because the '/' is ignored during indexing" ! basicSearch(split = Some('/'))

  }
}