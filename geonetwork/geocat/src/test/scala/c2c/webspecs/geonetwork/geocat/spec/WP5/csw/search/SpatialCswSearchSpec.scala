package c2c.webspecs
package geonetwork
package geocat
package spec.WP5.csw.search

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._


@RunWith(classOf[JUnitRunner])
class SpatialCswSearchSpec extends SearchSpecification {  def is =
  "Spatial search queries".title ^
  "This specification tests how spatial search queries"             					          										  ^ Step(setup)               ^
      "First import several metadata that are to be searched for" 								  										  ^ Step(importedMetadataId)  ^
      "The FR metadata contains the gemeinden bern extent" ^
      "The DE metadata contains the kantone vaud extent " ^
      "The EN metadata contains an extent that is within fribourg but shared a border with fribourg and vaud" ^
      "The XX metadata contains an extent that crosses the fribourg and vaud border is is within both" ^
      "A spatial search for all metadata ${contained by} ${kantone:bern} should return the ${FR} md "	        					             ! basicSearch  ^
      "A spatial search for all metadata ${contained by} ${gemeinden:bern} should return the ${FR} md "                                          ! basicSearch  ^
      "A spatial search for all metadata ${contained by} ${gemeinden:bern and kantone:bern} should return the ${FR} md "                         ! basicSearch  ^
      "A spatial search for all metadata ${contained by} ${gemeinden:bern or kantone:fribourg} should return the ${EN and FR} md "	             ! basicSearch  ^
      "A spatial search for all metadata ${contained by} ${kantone:bern or kantone:fribourg} should return the ${EN and FR} md "	             ! basicSearch  ^
      "A spatial search for all metadata ${contained by} ${kantone:fribourg} should return the ${EN} md "                                        ! basicSearch  ^
      "A spatial search for all metadata ${containing} ${gemeinden:lausanne} should return the ${DE} md "	        		                     ! basicSearch  ^
      "A spatial search for all metadata ${contained by} ${kantone:vaud,kantone:fribourg} should return the ${EN and DE and XX} md "	         ! basicSearch  ^
      "A spatial search for all metadata ${contained by} ${kantone:vaud} should return the ${DE} md "                                            ! basicSearch  ^
      "A spatial search for all metadata ${contained by} ${kantone:fribourg} should return the ${EN} md "	        		                     ! basicSearch  ^
                                                                                                  											       Step(tearDown)


  def basicSearch(implicit maxRecords:Int = 10000, similarity:Double = 1,lang:String = "fre") = (s: String) => {
    val (op, areas, expectedMetadata) = extract3(s)
    
    val filterFac = op.trim.toLowerCase() match {
      case "contained by" => csw.Within(_:String):OgcFilter
      case "containing" => Contains(_:String):OgcFilter
    }
    
    assert(!((areas contains "and") && (areas contains "or")), "Only one of 'and', ',' or 'or' is permitted")
    
    val areaCodes = areas.split("(or)|(and)|(,)").map{ _.toLowerCase.trim match {
          case "kantone:bern"       => "region:kantone:2"
          case "kantone:fribourg"   => "region:kantone:10"
          case "kantone:vaud"       => "region:kantone:22"
          case "gemeinden:bern"     => "region:gemeinden:351"
          case "gemeinden:lausanne" => "region:gemeinden:5586"
          case "gemeinden:ecublens" => "region:gemeinden:1002"
          case "gemeinden:vulliens" => "region:gemeinden:2222"
        }
    }

    val areaFilter = if (areas contains ",") {
      filterFac(areaCodes mkString ",")
    } else {
      val operator = if (areas contains "and") (_: OgcFilter) and (_: OgcFilter)
      else (_: OgcFilter) or (_: OgcFilter)
      val filters = areaCodes map filterFac
      (filters.tail foldLeft (filters.head: OgcFilter))(operator)
    }
    
    val xmlResponse = CswGetRecordsRequest(areaFilter.xml,
                                           resultType = ResultTypes.resultsWithSummary,
    									   outputSchema = OutputSchemas.Record, 
    									   maxRecords = maxRecords,
    									   url = lang+"/csw").execute().value
    
    find(xmlResponse, expectedMetadata)
  }
  
  lazy val extentFixture = GeocatFixture.reusableExtent(extentXml)
  override lazy val fixtures = Seq(extentFixture)
  val extentXml = 
    <gmd:extent xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gml="http://www.opengis.net/gml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <gmd:EX_Extent>
        <gmd:description xsi:type="gmd:PT_FreeText_PropertyType">
          <gco:CharacterString>fribourg and vaud</gco:CharacterString>
        </gmd:description>
        <gmd:geographicElement>
          <gmd:EX_BoundingPolygon>
            <gmd:extentTypeCode>
              <gco:Boolean>true</gco:Boolean>
            </gmd:extentTypeCode>
            <gmd:polygon>
              <gml:MultiSurface gml:id="N3527eb61b46b448a935f4bd31a8191c2">
                <gml:surfaceMember>
                  <gml:Polygon gml:id="N85a5be7d3c8d4547ad7cb30cacd3ab3e">
                    <gml:exterior>
                      <gml:LinearRing>
                        <gml:posList>6.807 46.51 6.806 46.594 6.95 46.595 6.951 46.511 6.807 46.51</gml:posList>
                      </gml:LinearRing>
                    </gml:exterior>
                  </gml:Polygon>
                </gml:surfaceMember>
              </gml:MultiSurface>
            </gmd:polygon>
          </gmd:EX_BoundingPolygon>
        </gmd:geographicElement>
      </gmd:EX_Extent>
    </gmd:extent>
  
}