package c2c.webspecs
package suite

import org.specs2.runner.SpecificationsFinder
import org.specs2.Specification
import java.text.SimpleDateFormat
import java.util.Date
import org.specs2.main.Arguments
import org.specs2.specification.Step
import c2c.webspecs.Properties
import scala.Option.option2Iterable

abstract class AbstractAllSpecs(title:String, specs: Class[_]*) extends Specification with SpecificationsFinder {
  Properties.specClass=specs(0)
  
  def is = {
    specs.
    	flatMap{s => createSpecification(s.getName)(Arguments())}.
    	foldLeft(initVal) { (res, cur) => res ^ link(cur) ^ Step(() => Thread.sleep(500)) }
    
  }

  private def initVal = title.title ^ sequential ^ ("For date: "+dateTime)

  private def dateTime = {
        val dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
        val date = new Date();
        dateFormat.format(date);
    }
  
}