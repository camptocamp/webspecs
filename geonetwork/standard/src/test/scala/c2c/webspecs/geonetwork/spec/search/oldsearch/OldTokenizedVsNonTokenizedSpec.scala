package c2c.webspecs
package geonetwork
package spec.search.oldsearch

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import c2c.webspecs.geonetwork.spec.search.AbstractTokenizedVsNonTokenizedSpec
import scala.xml.NodeSeq

@RunWith(classOf[JUnitRunner])
class OldTokenizedVsNonTokenizedSpec extends GeonetworkSpecification with SearchSpecification with AbstractTokenizedVsNonTokenizedSpec[NodeSeq] {
  
   def search(params: (String, String)*) = {
     hits(GetRequest("main.search.embedded", params :+ ('to -> 10) :_*).execute().value.getXml).size
  }
}