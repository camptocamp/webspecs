package c2c.webspecs.suite

import org.specs2.Specification
import org.specs2.runner.SpecificationsFinder
import c2c.webspecs.georchestra._
import c2c.webspecs.georchestra._
import c2c.webspecs.geoserver._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import java.text.SimpleDateFormat
import java.util.Date
import org.specs2.specification.Step
import org.specs2.main.Arguments

@RunWith(classOf[JUnitRunner])
class AllSpecs extends Specification with SpecificationsFinder { def is =
    examplesLinks("All Work Packages - "+dateTime)

    def examplesLinks(t: String) = {
  val specs = List(
      classOf[MapfishappSpec],
      classOf[ExtractorappSpec],
      classOf[SecuritySpec],
      classOf[WFSSpec]
	).flatMap{s => createSpecification(s.getName)(Arguments())}
      specs.foldLeft(initVal(t)) { (res, cur) => res ^ link(cur) }
    }

    def initVal(t:String) = t.title ^ sequential ^ Step(() => Thread.sleep(2000))

    def dateTime = {
        val dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        val date = new Date();
        dateFormat.format(date);
    }
}
