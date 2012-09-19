package c2c.webspecs.geoserver

import org.specs2.Specification
import org.specs2.runner.SpecificationsFinder
import java.text.SimpleDateFormat
import java.util.Date
import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import c2c.webspecs.Properties
import org.specs2.main.Arguments

@RunWith(classOf[JUnitRunner])
class AllSpecs extends Specification with SpecificationsFinder { 
  Properties.classLoader=classOf[AllSpecs].getClassLoader()
  
  def is =
    examplesLinks("index")

    def examplesLinks(t: String) = {
  val specs = List(
	  classOf[WFSSpec],
      classOf[IGNSpec],
	  classOf[TimeFilterSpec],
	  classOf[MinimumXPathSpec]
	).flatMap{s => createSpecification(s.getName)(Arguments())}
      specs.foldLeft(initVal(t)) { (res, cur) => res ^ link(cur) }
    }

    def initVal(t:String) = t.title ^ sequential ^ Step(() => Thread.sleep(2000)) ^ ("For date: "+dateTime)

    def dateTime = {
        val dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
        val date = new Date();
        dateFormat.format(date);
    }
}