package c2c.webspecs
package georchestra
import org.specs2.specification.Fragments
import org.specs2.specification.Step
import org.specs2.matcher.Matcher

abstract class GeorchestraSpecification extends WebSpecsSpecification[GeorchestraConfig]() {
  val config = new GeorchestraConfig(getClass().getSimpleName)
  def is = sequential ^ Step(setup) ^ isImpl ^ Step(tearDown)

  def isImpl: Fragments

}