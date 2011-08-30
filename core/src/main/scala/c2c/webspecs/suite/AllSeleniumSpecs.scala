package c2c.webspecs.suite

import org.specs2.SpecificationWithJUnit
import org.specs2.runner.SpecificationsFinder
import c2c.webspecs.geonetwork.geocat.spec.WP1._
import c2c.webspecs.geonetwork.geocat.spec.WP2._
import c2c.webspecs.geonetwork.geocat.spec.WP3._
import c2c.webspecs.geonetwork.geocat.spec.WP4._
import c2c.webspecs.geonetwork.geocat.spec.WP5._
import c2c.webspecs.geonetwork.geocat.spec.WP6._
import c2c.webspecs.geonetwork.geocat.spec.WP10._
import c2c.webspecs.geonetwork.geocat.spec.WP12._
import c2c.webspecs.geonetwork.geocat.spec.WP16._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import java.text.SimpleDateFormat
import java.util.Date
import org.specs2.specification.Step


@RunWith(classOf[JUnitRunner])
class AllSeleniumSpecs extends SpecificationWithJUnit with SpecificationsFinder { def is =
    examplesLinks("All Work Packages - "+dateTime)

    def examplesLinks(t: String) = {
  val specs = List(
	  classOf[SeleniumWP10],	  
	  classOf[SeleniumWP16]	  
	).flatMap{s => createSpecification(s.getName)}
      specs.foldLeft(initVal(t)) { (res, cur) => res ^ link(cur) }
    }

    def initVal(t:String) = t.title ^ sequential ^ Step(() => Thread.sleep(2000))

    def dateTime = {
        val dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        val date = new Date();
        dateFormat.format(date);
    }
}


class SeleniumWP10 extends SpecificationWithJUnit with SpecificationsFinder { def is =

	examplesLinks("WP 10: Reusable Object UI")

	def examplesLinks(t: String) = {
		val specs = List(
		    	classOf[CreateEditDeleteUserSeleniumSpec],
		    	classOf[ViewNonValidatedObjectsSeleniumSpec]
				).flatMap{s => createSpecification(s.getName)}
		specs.
		foldLeft(t.title) { (res, cur) => res ^ link(cur) }
	}
}

class SeleniumWP16 extends SpecificationWithJUnit with SpecificationsFinder { def is =

examplesLinks("WP 16: Miscellaneous Tasks")

def examplesLinks(t: String) = {
    val specs = List(
            classOf[SaveConfigurationSpec]
            ).flatMap{s => createSpecification(s.getName)}
    specs.
    foldLeft(t.title) { (res, cur) => res ^ link(cur) }
}
}