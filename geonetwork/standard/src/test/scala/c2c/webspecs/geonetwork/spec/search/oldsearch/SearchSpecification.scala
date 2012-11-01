package c2c.webspecs
package geonetwork
package spec.search.oldsearch

import org.specs2.execute.Result
import c2c.webspecs.geonetwork.spec.search.AbstractSearchSpecification
import scala.xml.NodeSeq

trait OldSearchSpecificationSupport {
   def summary(result: NodeSeq) = {
    val resultsDivText = (result \\ "div" filter (div => (div @@ "class") == List("results_title"))).text.replaceAll("\r|\n|\\s+"," ")
    val Extractor = """.+?(\d+)-(\d+)/(\d+).*?\(.+?(\d+)/(\d+)\).*""".r
    val Extractor(_startHit, _endHits, _totalHits, _currentPage, _totalPages) = resultsDivText
    new {
      val startHit = _startHit
      val endHits = _endHits
      val totalHits = _totalHits
      val currentPage = _currentPage
      val totalPages = _totalPages
    }
  }
  def hits(records: NodeSeq) = (records \\ "div").filter(div => (div @@ "class") contains "hit")
}
trait SearchSpecification extends AbstractSearchSpecification[NodeSeq] with OldSearchSpecificationSupport{
  self:GeonetworkSpecification =>
  def titleExtension = "Old"
    
  
  def findCodesFromResults(records: NodeSeq) = {
    val showHrefs = (hits(records) \\ "a").flatMap(_ @@ "href").filter(_.contains("metadata.show"))
    val idExtractor = ".*id=(.+?)\\&.+".r
    val ids = showHrefs.flatMap(h => idExtractor.unapplySeq(h).getOrElse(Nil))
    ids flatMap idToLocalMap.get
  }

}