package c2c.webspecs.suite

import org.specs2.Specification
import org.specs2.runner.SpecificationsFinder
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import java.text.SimpleDateFormat
import java.util.Date
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.spec._


@RunWith(classOf[JUnitRunner])
class AllSpecs extends Specification with SpecificationsFinder { def is =
    examplesLinks("All Work Packages - "+dateTime)

    def examplesLinks(t: String) = {
  val specs = List(
      classOf[CSWSpecs],
      classOf[SampleDataSpecs],
      classOf[SearchSpecs]
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


class CSWSpecs extends Specification with SpecificationsFinder { def is =
    examplesLinks("Csw related tests")

    def examplesLinks(t: String) = {
  val specs = List(
        classOf[csw.CswTransactionUpdateSpec],
        classOf[csw.CswGetCapabilitiesServiceUrlSpec],
        classOf[csw.CswLanguageSpec],
        classOf[csw.CswOutputSchemaSpec],
        classOf[csw.CswGetRecordsSpec],
        classOf[csw.CswGetRecordsByIdSpec],
        classOf[csw.CswOutputSchemaSpec]
      ).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

}

class SampleDataSpecs extends Specification with SpecificationsFinder { def is =
    examplesLinks("Sample data related tests")

    def examplesLinks(t: String) = {
  val specs = List(
        classOf[sampledata.AddSampleDataSpec]
      ).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

}

class SearchSpecs extends Specification with SpecificationsFinder { def is =
    examplesLinks("Search related tests")

    def examplesLinks(t: String) = {
  val specs = List(
        classOf[search.SelectAllBugSpec],
        classOf[search.BasicXmlSearchSpec],
        classOf[search.SummaryAccuracySpec]
      ).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

}

