package c2c.webspecs
package geonetwork
package geocat
package spec.WP5

import c2c.webspecs.geonetwork.geocat.GeocatSpecification
import org.specs2.specification.Step
import scala.xml.Node
import csw._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SearchOrderSpec 
	extends c2c.webspecs.geonetwork.spec.csw.search.SearchOrderSpec 
	with GeocatSpecification {
  override def pathToSearchMetadata = "/geocat/data/csw/search/"
}