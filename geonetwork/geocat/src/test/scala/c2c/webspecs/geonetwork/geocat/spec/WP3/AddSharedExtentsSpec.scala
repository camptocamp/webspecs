package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2._
import specification._
import java.util.UUID
import scala.xml.NodeSeq
import javax.xml.transform.Source

class AddSharedExtentsSpec extends GeonetworkSpecification { def is =
  "This specification tests creating shared extent by passing in a extent xml snippet"                          ^ Step(setup) ^ t ^
    "Calling shared.process with the xml snippet for adding a extent"                                           ^ extentAdd.toGiven ^
    "Should have 200 result"                                                                                    ^ a200ResponseThen.narrow[Response[NodeSeq]] ^
    "Extent node should have an xlink href"                                                                     ^ hrefInElement.toThen ^
    "xlink href should retrieve the full extent"                                                                ^ xlinkGetElement.toThen ^
    "Will result in a new shared extent"                                                                        ! newExtent ^
                                                                                                                  end ^
                                                                                                                  Step(deleteNewExtent) ^
                                                                                                                  Step(tearDown)

  val extentAdd = () => (config.adminLogin then ProcessSharedObject(extentXML))(None)
  val hrefInElement = (result:Response[NodeSeq]) => (result.value \\ "extent" \@ "xlink:href") must not beEmpty
  val xlinkGetElement = (result:Response[NodeSeq]) => {
    val href = (result.value \\ "extent" \@ "xlink:href")(0)
    val xlink = GetRequest(href)(None)
    (xlink must haveA200ResponseCode) and
      (xlink.value.withXml{_ \\ "LocalisedCharacterString" map (_.text.trim) must contain (deValue,enValue,frValue,deValue+2,enValue+2,frValue+2)})
  }

  val Search = SearchExtent(typeName = List(Extents.NonValidated))
  def newExtent = Search(uuid).value must not beEmpty

  def deleteNewExtent = Search(uuid).value.foreach{c => DeleteExtent(Extents.NonValidated, c.id)}

  lazy val uuid = UUID.randomUUID().toString
  lazy val deValue = uuid+"de*automated*"
  lazy val enValue = uuid+"en*automated*"
  lazy val frValue = uuid+"fr*automated*"
  lazy val extentXML =  <gmd:extent xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gml="http://www.opengis.net/gml" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gmd="http://www.isotc211.org/2005/gmd">
        <gmd:EX_Extent>
          <gmd:description xsi:type="gmd:PT_FreeText_PropertyType">
            <gmd:PT_FreeText>
              <gmd:textGroup>
                <gmd:LocalisedCharacterString locale="#DE">{deValue}</gmd:LocalisedCharacterString>
              </gmd:textGroup>
              <gmd:textGroup>
                <gmd:LocalisedCharacterString locale="#EN">{enValue}</gmd:LocalisedCharacterString>
              </gmd:textGroup>
              <gmd:textGroup>
                <gmd:LocalisedCharacterString locale="#FR">{frValue}</gmd:LocalisedCharacterString>
              </gmd:textGroup>
            </gmd:PT_FreeText>
          </gmd:description>
          <gmd:geographicElement>
            <gmd:EX_GeographicDescription>
              <gmd:geographicIdentifier>
                <gmd:MD_Identifier>
                  <gmd:code xsi:type="gmd:PT_FreeText_PropertyType">
                    <gmd:PT_FreeText>
                      <gmd:textGroup>
                        <gmd:LocalisedCharacterString locale="#DE">{deValue}2</gmd:LocalisedCharacterString>
                      </gmd:textGroup>
                      <gmd:textGroup>
                        <gmd:LocalisedCharacterString locale="#EN">{enValue}2</gmd:LocalisedCharacterString>
                      </gmd:textGroup>
                      <gmd:textGroup>
                        <gmd:LocalisedCharacterString locale="#FR">{frValue}2</gmd:LocalisedCharacterString>
                      </gmd:textGroup>
                    </gmd:PT_FreeText>
                  </gmd:code>
                </gmd:MD_Identifier>
              </gmd:geographicIdentifier>
            </gmd:EX_GeographicDescription>
          </gmd:geographicElement>
          <gmd:geographicElement>
            <gmd:EX_BoundingPolygon>
              <gmd:extentTypeCode>
                <gco:Boolean>false</gco:Boolean>
              </gmd:extentTypeCode>
              <gmd:polygon>
                <gml:MultiSurface gml:id="Nce2760481f384af9881b23592f0d0eb0">
                  <gml:surfaceMember>
                    <gml:Polygon gml:id="N3b9677fd00fb47eda5417cdc59b1a6f1">
                      <gml:exterior>
                        <gml:LinearRing>
                          <gml:posList>7.605 47.221 7.672 47.223 7.647 47.191 7.617 47.195 7.588 47.202 7.559 47.208 7.521 47.212 7.553 47.205 7.569 47.177 7.55 47.159 7.52 47.158 7.502 47.162 7.501 47.17 7.508 47.182 7.498 47.195 7.46 47.198 7.461 47.184 7.467 47.165 7.465 47.161 7.438 47.17 7.428 47.187 7.427 47.199 7.458 47.209 7.528 47.22 7.605 47.221</gml:posList>
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
                <gco:Boolean>false</gco:Boolean>
              </gmd:extentTypeCode>
              <gmd:westBoundLongitude>
                <gco:Decimal>7.427</gco:Decimal>
              </gmd:westBoundLongitude>
              <gmd:eastBoundLongitude>
                <gco:Decimal>7.672</gco:Decimal>
              </gmd:eastBoundLongitude>
              <gmd:southBoundLatitude>
                <gco:Decimal>47.158</gco:Decimal>
              </gmd:southBoundLatitude>
              <gmd:northBoundLatitude>
                <gco:Decimal>47.223</gco:Decimal>
              </gmd:northBoundLatitude>
            </gmd:EX_GeographicBoundingBox>
          </gmd:geographicElement>
        </gmd:EX_Extent>
      </gmd:extent>
}