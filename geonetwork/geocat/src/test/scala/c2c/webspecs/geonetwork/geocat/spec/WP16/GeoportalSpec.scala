package c2c.webspecs
package geonetwork
package geocat
package spec.WP16

import scala.xml.NodeSeq
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import csw._

@RunWith(classOf[JUnitRunner])
class GeoportalSpec extends GeocatSpecification {  def is =
	"Geoportal API".title 															 							^ Step(setup) ^
	"For this specification a few metadata must be imported with the 'special' geoportal keyword"   			^ Step(importMd(3,"/geocat/data/comprehensive-iso19139che.xml",datestamp)) ^
	allSearches("e-geo.ch geoportal") ^ 
	allSearches("géoportail e-geo.ch") ^ 
	allSearches("geoportale e-geo.ch") ^  
	"Csw CQL and XML searches for metadata with valid hrefs in the linkage element must return the geoportal metadata"  ! searchHref ^
	"Given one of the metadata records as a CSW GetRecord Response in iso"  							   ^ getCswRecord.toGiven ^
	"The root tag should be gmd:MD_Metadata"									  						   ^ haveCorrectRoot.toThen ^
	"Should have nonEmpty linkage tags"										  							   ^ haveNonEmptyLinkage.toThen ^
																												Step(tearDown)

  def allSearches(term: String) = {
	def withLang(search: String, lang: String) = {
	  "in "+lang+" must return the geoportal metadata"		! searchKeyword(term, search, lang)
	}
	def withSearchType(search: String) = {
	  "using "+search+" searches" ^
	  withLang(search, "french")
//	  withLang(search, "german") ^
//	  withLang(search, "english") ^
//	  withLang(search, "italian")  
	}
	"Searching for "+term ^ 
//	 	withSearchType("Csw CQL") ^
	 withSearchType("Csw XML") ^
	 withSearchType("XmlSearch") ^ endp
  }
  val guardCondition = PropertyIsEqualTo("_defaultTitle", "Comprehenisve Test "+datestamp)
  def searchKeyword(keyword: String, searchType: String, language: String) = {
    val query = PropertyIsEqualTo("keyword", keyword) and guardCondition 

    implicit val uriResolver = new GeonetworkURIResolver {
      override def locale = language match {
        case "german" => "ger"
        case "english" => "eng"
        case "french" => "fre"
        case "italian" => "ita"
      } 
    }
    val recordsFound = searchType match {
      case "Csw CQL" =>
        hits(CswCQLGetRecordsRequest(query.cql).execute().value.getXml)
      case "Csw XML" =>
        hits(CswGetRecordsRequest(query.xml).execute().value.getXml)
      case "XmlSearch" =>
        XmlSearch().search('keyword -> keyword, guardCondition.name -> guardCondition.literal).execute().value.count
    }

    recordsFound aka searchType must_== 3
  }

  def searchHref = {
    val query = PropertyIsEqualTo("hasLinkageURL", "y") and guardCondition
    val cqlHits = hits(CswCQLGetRecordsRequest(query.cql).execute().value.getXml)
    val xmlHits = hits(CswGetRecordsRequest(query.xml).execute().value.getXml)

    (cqlHits must_== 3) and
      (xmlHits must_== 3)
  }

  def hits(response: NodeSeq) = (response \\ "SearchResults" \@ "numberOfRecordsMatched").head.toInt
  
  val getCswRecord = () => {
    CswCQLGetRecordsRequest(guardCondition.cql, resultType=ResultTypes.results).execute().value.getXml \\ "SearchResults" \ "_"
  }
  val haveCorrectRoot = (doc:NodeSeq) => {
	  doc.headOption.map(_.label) must beSome("MD_Metadata")
  }
  val haveNonEmptyLinkage = (doc:NodeSeq) => {
	  val urls = doc \\ "transferOptions" \\ "CI_OnlineResource" \\ "linkage" \\ "URL" find (_.text contains datestamp)
	  urls must beSome
  }
}
