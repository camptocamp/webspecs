package c2c.webspecs
package inspire

import org.specs2.specification.Fragments
import org.specs2.specification.Step
import org.specs2.matcher.Matcher

abstract class InspireSpecification extends WebSpecsSpecification[InspireConfig]() {
  val config = new InspireConfig(getClass().getSimpleName)
  def is = sequential ^ Step(setup) ^ isImpl ^ Step(tearDown)

  def isImpl: Fragments
  
}