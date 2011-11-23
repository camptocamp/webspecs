package c2c.webspecs
package geonetwork
package geocat
package spec.WP16

import scala.xml.NodeSeq
import org.apache.http.entity.mime.content.StringBody
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.ImportStyleSheets.NONE
import c2c.webspecs.geonetwork.csw.OutputSchemas._
import org.specs2.matcher.Matcher
import csw._
import scala.xml.Node

@RunWith(classOf[JUnitRunner]) 
class GeoportalSpec extends GeocatSpecification {  def is =
	"Geoportal API".title 															 							^ Step(setup) ^
	"For this specification a few metadata must be imported with the 'special' geoportal keyword"   			^ Step(importMd(3,"/geocat/data/comprehensive-iso19139che.xml",datestamp)) ^
	"Csw CQL and XML searches for ${e-geo.ch Geoportal} in ${german} must return the geoportal metadata"		! searchKeyword ^
	"Csw CQL and XML searches for ${e-geo.ch geoportal} in ${english} must return the geoportal metadata"		! searchKeyword ^
	"Csw CQL and XML searches for ${géoportail e-geo.ch} in ${french} must return the geoportal metadata"		! searchKeyword ^
	"Csw CQL and XML searches for ${géoportail e-geo.ch} in ${german} must return the geoportal metadata"		! searchKeyword ^
	"Csw CQL and XML searches for ${geoportale e-geo.ch} in ${italian} must return the geoportal metadata"	    ! searchKeyword ^
	"Csw CQL and XML searches for metadata with valid hrefs in the linkage element must return the geoportal metadata"  ! searchHref ^
	"Given one of the metadata records as a CSW GetRecord Response in iso"  							   ^ getCswRecord.toGiven ^
	"The root tag should be gmd:MD_Metadata"									  						   ^ haveCorrectRoot.toThen ^
	"Should have nonEmpty linkage tags"										  							   ^ haveNonEmptyLinkage.toThen ^
																												Step(tearDown)

  val guardCondition = PropertyIsEqualTo("_defaultTitle", "Comprehenisve Test "+datestamp)
  val searchKeyword = (input: String) => {
    val (keyword, language) = extract2(input)
    val query = PropertyIsEqualTo("keyword", keyword) and guardCondition 

    implicit val uriResolver = new GeonetworkURIResolver {
      override def locale = language match {
        case "german" => "deu"
        case "english" => "eng"
        case "french" => "fra"
        case "italian" => "ita"
      } 
    }

    val cqlHits = hits(CswCQLGetRecordsRequest(query.cql).execute().value.getXml)
    val xmlHits = hits(CswGetRecordsRequest(query.xml).execute().value.getXml)

    (cqlHits aka "cql" must_== 3) and
      (xmlHits aka "xml" must_== 3)
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
