package c2c.webspecs.geonetwork
package spec

import org.specs2.Specification
import org.specs2.SpecificationWithJUnit
import org.specs2.runner.SpecificationsFinder

class AllGeonetworkSpecifications extends Specification with SpecificationsFinder { def is =

    examplesLinks("WP 1:  Shared Objects")

    def examplesLinks(t: String) = {
  val specs = List(
      classOf[CreateSpec],
      classOf[EditSpec],
      classOf[GetUserSpec],
      classOf[ImportSpec]
      ).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

}