package c2c.webspecs
package geoserver
import org.specs2.specification.Fragments
import org.specs2.specification.Step
import org.specs2.matcher.Matcher
import org.specs2.Specification

abstract class GeoserverSpecification extends WebSpecsSpecification[GeoserverConfig]() {
  val config = new GeoserverConfig(getClass().getSimpleName)
  def is = sequential ^ Step(setup) ^ isImpl ^ Step(tearDown) ^ end

  def isImpl: Fragments
  
}