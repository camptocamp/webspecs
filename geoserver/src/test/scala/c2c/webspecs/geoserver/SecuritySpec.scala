package c2c.webspecs
package geoserver
import c2c.webspecs.WebSpecsSpecification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.Matcher
import org.specs2.execute.Result

@RunWith(classOf[JUnitRunner])
class SecuritySpec extends GeoserverSpecification {
  def isImpl =
    "Security Spec".title ^
      "This Spec test WFS API" ^
      "A GetMap request of a protected layer result a 401 error" ! protectedNonAuthorized ^
      "A GetMap request of a protected layer when authorized must result in an image" ! protectedAuthorized

  def protectedNonAuthorized = {
    pending
  }
  def protectedAuthorized = {
    pending
  }
}