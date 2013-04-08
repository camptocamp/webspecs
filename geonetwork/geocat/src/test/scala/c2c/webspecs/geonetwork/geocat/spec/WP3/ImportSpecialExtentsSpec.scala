package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import scala.xml.NodeSeq
import scala.xml.Node
import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import shared._

@RunWith(classOf[JUnitRunner]) 
class ImportSpecialExtentsSpec extends GeocatSpecification { def is =
  "Shared Extent import corner cases".title ^ Step(setup) ^
  "This specification tests some of the corner cases associated with importing extents"  ^
      "Importing an extent with only a geoDesc"                            ^ ImportObj(geoDesc).toGiven ^
      "should result in a successful http request"                       ^ a200ResponseThen.narrow[Response[NodeSeq]] ^
      "should not result in a new Shared object (no xlink on extent)"    ^ noXlink("extent").toThen ^
                                                                           endp ^ 
      "Importing an extent with a temporalExtent but no Geom"            ^ ImportObj(temporalExtent(Nil)).toGiven ^
      "should result in a successful http request"                       ^ a200ResponseThen.narrow[Response[NodeSeq]] ^
      "should not result in a new Shared object (no xlink on extent)"    ^ noXlink("spatialExtent").toThen ^
                                                                           endp ^ 
      "Importing an extent with a temporalExtent with a Geom"            ^ ImportObj(temporalExtent(spatialExtent)).toGiven ^
      "should result in a successful http request"                       ^ a200ResponseThen.narrow[Response[NodeSeq]] ^
      "should result in a Shared object (no xlink on extent)"            ^ noXlink("extent").toThen ^
      "but should have a Shared object on spatialExtent"                 ^ noXlink("spatialExtent").toThen ^
                                                                           endp ^ 
      "Importing an extent with a verticalExtent "                       ^ ImportObj(verticalExtent).toGiven ^
      "should result in a successful http request"                       ^ a200ResponseThen.narrow[Response[NodeSeq]] ^
      "should not result in a new Shared object (no xlink on extent)"    ^ noXlink("extent").toThen ^
                                                                           endp ^ 
                                                                       Step(tearDown)
     
                                                                           
  def ImportObj(xml:Node) = 
    () => (config.adminLogin then ProcessSharedObject(xml,addOnly=false)).execute();
  
  def noXlink(extentTag:String) = (resp:Response[NodeSeq]) => {(resp.value \\ extentTag \@ "xlink:href") must beEmpty}
  def xlink(extentTag:String) = (resp:Response[NodeSeq]) => (resp.value \\ extentTag \@ "xlink:href") must not (beEmpty)
  
  val geoDesc = 
            <gmd:extent xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:srv="http://www.isotc211.org/2005/srv" xmlns:gml="http://www.opengis.net/gml" >
                <gmd:EX_Extent>
                    <gmd:description>
                        <gco:CharacterString>A Desc</gco:CharacterString>
                    </gmd:description>
                    <gmd:geographicElement>
                        <gmd:EX_GeographicDescription>
                            <gmd:extentTypeCode>
                                <gco:Boolean>1</gco:Boolean>
                            </gmd:extentTypeCode>
                            <gmd:geographicIdentifier>
                                <gmd:MD_Identifier>
                                    <gmd:code>
                                        <gco:CharacterString>some auth code</gco:CharacterString>
                                    </gmd:code>
                                </gmd:MD_Identifier>
                            </gmd:geographicIdentifier>
                        </gmd:EX_GeographicDescription>
                    </gmd:geographicElement>
                </gmd:EX_Extent>
            </gmd:extent>
    
    def temporalExtent(extent:NodeSeq) = 
            <gmd:extent xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:srv="http://www.isotc211.org/2005/srv" xmlns:gml="http://www.opengis.net/gml" >
                <gmd:EX_Extent>
                    <gmd:temporalElement>
                        <gmd:EX_SpatialTemporalExtent>
                            <gmd:extent>
                                <gml:TimePeriod gml:id="N101FF">
                                </gml:TimePeriod>
                            </gmd:extent>
                            {extent}
                        </gmd:EX_SpatialTemporalExtent>
                    </gmd:temporalElement>
                </gmd:EX_Extent>
            </gmd:extent>
      
      val spatialExtent = 
        <gmd:spatialExtent>
            <gmd:EX_Extent>
                <gmd:description xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="gmd:PT_FreeText_PropertyType">
                    <gco:CharacterString>Renan (BE)</gco:CharacterString>
                </gmd:description>
                <gmd:geographicElement>
                    <gmd:EX_GeographicDescription>
                        <gmd:geographicIdentifier>
                            <gmd:MD_Identifier>
                                <gmd:code xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="gmd:PT_FreeText_PropertyType">
                                    <gco:CharacterString>Renan (BE)</gco:CharacterString>
                                </gmd:code>
                            </gmd:MD_Identifier>
                        </gmd:geographicIdentifier>
                    </gmd:EX_GeographicDescription>
                </gmd:geographicElement>
                <gmd:geographicElement>
                    <gmd:EX_BoundingPolygon>
                        <gmd:extentTypeCode>
                            <gco:Boolean>true</gco:Boolean>
                        </gmd:extentTypeCode>
                        <gmd:polygon>
                            <gml:MultiSurface gml:id="Neb34cc5dc1d34bd08405ebf13d1565ef">
                                <gml:surfaceMember>
                                    <gml:Polygon gml:id="N09101de3a34e427dae73e1e62cadc4f1">
                                        <gml:exterior>
                                            <gml:LinearRing>
                                                    <gml:posList>6.867 47.085 6.872 47.103 6.881 47.108 6.886 47.124 6.901 47.121 6.911 47.122 6.934 47.136 6.938 47.129 6.931 47.125 6.939 47.11 6.927 47.109 6.918 47.105 6.886 47.092 6.867 47.085</gml:posList>
                                            </gml:LinearRing>
                                        </gml:exterior>
                                    </gml:Polygon>
                                </gml:surfaceMember>
                            </gml:MultiSurface>
                        </gmd:polygon>
                    </gmd:EX_BoundingPolygon>
                </gmd:geographicElement>
                <gmd:geographicElement>
                    <gmd:EX_GeographicBoundingBox>
                        <gmd:extentTypeCode>
                            <gco:Boolean>true</gco:Boolean>
                        </gmd:extentTypeCode>
                        <gmd:westBoundLongitude>
                            <gco:Decimal>6.867</gco:Decimal>
                        </gmd:westBoundLongitude>
                        <gmd:eastBoundLongitude>
                            <gco:Decimal>6.939</gco:Decimal>
                        </gmd:eastBoundLongitude>
                        <gmd:southBoundLatitude>
                            <gco:Decimal>47.085</gco:Decimal>
                        </gmd:southBoundLatitude>
                        <gmd:northBoundLatitude>
                            <gco:Decimal>47.136</gco:Decimal>
                        </gmd:northBoundLatitude>
                    </gmd:EX_GeographicBoundingBox>
                </gmd:geographicElement>
            </gmd:EX_Extent>
        </gmd:spatialExtent>

  val verticalExtent = 
            <gmd:extent xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:srv="http://www.isotc211.org/2005/srv" xmlns:gml="http://www.opengis.net/gml" >
                <gmd:EX_Extent>
                    <gmd:verticalElement>
                        <gmd:EX_VerticalExtent>
                            <gmd:minimumValue>
                                <gco:Real>0</gco:Real>
                            </gmd:minimumValue>
                            <gmd:maximumValue>
                                <gco:Real>32</gco:Real>
                            </gmd:maximumValue>
                        </gmd:EX_VerticalExtent>
                    </gmd:verticalElement>
                </gmd:EX_Extent>
            </gmd:extent>

}