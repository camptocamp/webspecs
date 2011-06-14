package c2c.webspecs.suite

import org.specs2.Specification
import org.specs2.SpecificationWithJUnit
import org.specs2.runner.SpecificationsFinder
import c2c.webspecs.geonetwork.spec.{AllGeonetworkSpecifications,CreateSpec,EditSpec,GetUserSpec}

class AllSpecs extends SpecificationWithJUnit with SpecificationsFinder { def is =
    examplesLinks("All Workpackages")

    def examplesLinks(t: String) = {
  val specs = List(
      classOf[AllGeonetworkSpecifications],classOf[CreateSpec],
      classOf[WP2]
      ).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

}

class WP2 extends SpecificationWithJUnit with SpecificationsFinder { def is =

    examplesLinks("WP 2: Migrate Data")

    def examplesLinks(t: String) = {
  val specs = List(
      classOf[GetUserSpec],
      classOf[c2c.webspecs.geonetwork.spec.ImportSpec]
      ).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

}