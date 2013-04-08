package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2._
import specification._
import scala.xml.NodeSeq
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import shared._

@RunWith(classOf[JUnitRunner]) 
class AddSharedExtentsSpec extends GeocatSpecification { def is =
  "This specification tests creating shared extent by passing in a extent xml snippet"                           ^ Step(setup) ^
    "This subfragment tests a gmd_complete extent with exclude extent type code"                                 ^ testExtent(ExtentFormat.gmd_complete,0) ^ p ^
    "This subfragment tests a gmd_bbox extent with exclude extent type code"                                     ^ testExtent(ExtentFormat.gmd_bbox,0) ^ p ^
    "This subfragment tests a gmd_complete extent with include extent type code"                                 ^ testExtent(ExtentFormat.gmd_complete,1) ^ p ^
    "This subfragment tests a gmd_bbox extent with exclude extent type code"                                     ^ testExtent(ExtentFormat.gmd_bbox,1) ^ p ^
    "This subfragment tests updating an existing extent"														 ^ updateExtentSpec ^ 
                                                                                                                   Step(tearDown)

  def testExtent(format:ExtentFormat.Value,code:Int):Fragments = {
    "Calling shared.process with the xml snippet for adding a ${"+format+"} extent and extentTypeCode=${"+code+"}"     ^ extentAdd.toGiven ^
	    "Should be a successful http request (200 response code)"                                                      ^ a200ResponseThen.narrow[Response[NodeSeq]] ^
	    "Extent node should have an xlink href with ${"+format+"} and extentTypeCode=${"+code+"}"                      ^ hrefInElementWithString.toThen ^
        "Should have the correct ${host} in the xlink created during processing of shared object"                      ^ hrefHost("extent").toThen ^ 
        "Should have the correct ${port} in the xlink created during processing of shared object"                      ^ hrefHost("extent").toThen ^ 
	    "xlink href should retrieve the full extent with ${"+format+"} and extentTypeCode=${"+code+"}"                 ^ xlinkGetElement.toThen ^ end ^
	    "Will result in a new shared extent"                                                                           ! newExtent ^
	    "Deleting the extent"                                                                                          ^ deleteNewExtent.toGiven ^
    "must correctly remove the extent from the system"                                                                 ^ noSharedExtent.toThen ^ end

  }

  def updateExtentSpec:Fragments = {
    "First import a known extent (${gmd_bbox} ${0})"														   ^ extentAdd.toGiven ^
    "Then update the extent to have a new geometry"								 							   ^ updateGeom.toWhen ^
    "The request should succeed with a 200 result"															     ^ a200ResponseThen.narrow[Response[XmlValue]] ^
    "The resulting extent should have the new geometry"														   ^ newBBox.toThen ^ end ^
    "Deleting the extent"                                                                                      ^ Step(deleteNewExtent)
  }

  val extentAdd = (s:String) => {
    val (format,code) = extract2(s)
    val xml:NodeSeq = ExtentFormat.withName(format) match {
      case ExtentFormat.gmd_bbox =>
        bbox(code.toInt)
      case ExtentFormat.gmd_complete =>
        Seq(bbox(code.toInt),polygon(code.toInt))
    }

    val fullExtent = extentXML(xml)
    (config.adminLogin then ProcessSharedObject(fullExtent, addOnly = true)).execute()
  }
  val hrefInElementWithString = (result:Response[NodeSeq],s:String) => {
    val (format,extentTC) = extract2(s)
    val typeCode = extentTC.toInt match {case 0 => "false"; case 1 => "true"}
    
    val href = (result.value \\ "extent" \@ "xlink:href").head

    (href must not beEmpty) and
      (href must =~ ("extentTypeCode="+typeCode)) and
      (href must =~ ("typename=gn:non_validated")) and
      (href.toLowerCase must =~ ("format="+format))
  }
  val xlinkGetElement = (result:Response[NodeSeq],s:String) => {
    val (format,typeCode) = extract2(s)

    val href = (result.value \\ "extent" \@ "xlink:href").head
    val xlink = ResolveXLink.execute(href)
    val localisedNodes = xlink.value.withXml{_ \\ "LocalisedCharacterString" map (_.text.trim)}
    val extentTypeCode = xlink.value.withXml{_ \\ "extentTypeCode"}
    val polygon = xlink.value.withXml{_ \\ "polygon"}
    val bbox = xlink.value.withXml{_ \\ "EX_GeographicBoundingBox"}

    val typeCodeAndPolygonAssertions =
      if(format == ExtentFormat.gmd_complete.toString) {
        (polygon must haveSize (1)) and
        (polygon must \\("LinearRing")) and
        (extentTypeCode must haveSize (2)) and
        (extentTypeCode(0).text must_== typeCode) and
        (extentTypeCode(1).text must_== typeCode)
      } else {
        (extentTypeCode must haveSize (1)) and
        (extentTypeCode(0).text must_== typeCode)
      }

	    
	  val westBoundLongitude = (bbox \\ "westBoundLongitude").text.trim.toDouble
	  val southBoundLatitude = (bbox \\ "southBoundLatitude").text.trim.toDouble
	  val eastBoundLongitude = (bbox \\ "eastBoundLongitude").text.trim.toDouble
	  val northBoundLatitude = (bbox \\ "northBoundLatitude").text.trim.toDouble
	
	  val obtainedBounds = (westBoundLongitude,southBoundLatitude,eastBoundLongitude,northBoundLatitude)
	  
      (xlink must haveA200ResponseCode) and
        (localisedNodes must contain (deValue,enValue,frValue,deValue+2,enValue+2,frValue+2)) and
        (bbox must haveSize (1)) and
        (obtainedBounds must_== bbox1) and
        typeCodeAndPolygonAssertions

  }

  val Search = SearchExtent(typeName = List(Extents.NonValidated))
  def newExtent = Search.execute(uuid.toString).value must not beEmpty

  val deleteNewExtent = () => Search.execute(uuid.toString).value.flatMap{c =>
    val request = config.adminLogin then DeleteExtent(Extents.NonValidated, c.id, true) then Search.copy(property = Extents.IdProperty).setIn(c.id)
    request.execute().value
  }

  val noSharedExtent = (_:List[ExtentSummary]) must beEmpty

  val updateGeom = (addResponse:Response[NodeSeq]) => {
    val href = addResponse.value \\ "extent" \@ "xlink:href" head
    
    val ID = """.*id=(\d*?)&.*""".r
    val ID(id) = href
    
    val noHrefXml = extentXML(bbox(0,bbox2));
    
    val xml =     
      <gmd:extent
    		xmlns:xlink="http://www.w3.org/1999/xlink" 
    		xlink:href={"http://www.geocat.ch:80/geonetwork/srv/eng/xml.extent.get?wfs=default&format=gmd_complete&typename=gn:non_validated&id="+id+"&extentTypeCode=true"} 
    		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gml="http://www.opengis.net/gml" 
    		xmlns:gco="http://www.isotc211.org/2005/gco" 
    		xmlns:gmd="http://www.isotc211.org/2005/gmd">
    		{noHrefXml.child}
      </gmd:extent>
    
    val updateResponse = UpdateSharedObject(xml).execute()

    val getRequest = GetRequest(href).execute()
    
    updateResponse.map{_ => getRequest.value}
  }

  val newBBox = (updateResponse:Response[XmlValue]) => {
    val xml = updateResponse.value.withXml(i => i)
    
    val westBoundLongitude = (xml \\ "westBoundLongitude").text.trim.toDouble
    val southBoundLatitude = (xml \\ "southBoundLatitude").text.trim.toDouble
    val eastBoundLongitude = (xml \\ "eastBoundLongitude").text.trim.toDouble
    val northBoundLatitude = (xml \\ "northBoundLatitude").text.trim.toDouble
    
    (westBoundLongitude, southBoundLatitude, eastBoundLongitude, northBoundLatitude) must_== bbox2
  }
  
  lazy val deValue = uuid+"de*automated*"
  lazy val enValue = uuid+"en*automated*"
  lazy val frValue = uuid+"fr*automated*"
  def extentXML(geo:NodeSeq) =
    <gmd:extent xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gml="http://www.opengis.net/gml" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gmd="http://www.isotc211.org/2005/gmd">
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
          {geo}
        </gmd:EX_Extent>
      </gmd:extent>

  def bbox(extentTypeCode:Int, bbox:(Double,Double,Double,Double) = bbox1) =
    <gmd:geographicElement>
      <gmd:EX_GeographicBoundingBox>
        <gmd:extentTypeCode>
          <gco:Boolean>{extentTypeCode}</gco:Boolean>
        </gmd:extentTypeCode>
        <gmd:westBoundLongitude>
          <gco:Decimal>{bbox._1}</gco:Decimal>
        </gmd:westBoundLongitude>
        <gmd:southBoundLatitude>
          <gco:Decimal>{bbox._2}</gco:Decimal>
        </gmd:southBoundLatitude>
        <gmd:eastBoundLongitude>
          <gco:Decimal>{bbox._3}</gco:Decimal>
        </gmd:eastBoundLongitude>
        <gmd:northBoundLatitude>
          <gco:Decimal>{bbox._4}</gco:Decimal>
        </gmd:northBoundLatitude>
      </gmd:EX_GeographicBoundingBox>
    </gmd:geographicElement>

  def polygon(extentTypeCode:Int, geom:String = poly1) =
    <gmd:geographicElement>
      <gmd:EX_BoundingPolygon>
        <gmd:extentTypeCode>
          <gco:Boolean>{extentTypeCode}</gco:Boolean>
        </gmd:extentTypeCode>
        <gmd:polygon>
          <gml:MultiSurface gml:id="Nce2760481f384af9881b23592f0d0eb0">
            <gml:surfaceMember>
              <gml:Polygon gml:id="N3b9677fd00fb47eda5417cdc59b1a6f1">
                <gml:exterior>
                  <gml:LinearRing>
                    <gml:posList>{geom}</gml:posList>
                  </gml:LinearRing>
                </gml:exterior>
              </gml:Polygon>
            </gml:surfaceMember>
          </gml:MultiSurface>
        </gmd:polygon>
      </gmd:EX_BoundingPolygon>
    </gmd:geographicElement>
  
 def poly1 = "7.605 47.221 7.672 47.223 7.647 47.191 7.617 47.195 7.588 47.202 7.559 47.208 7.521 47.212 7.553 47.205 7.569 47.177 7.55 47.159 7.52 47.158 7.502 47.162 7.501 47.17 7.508 47.182 7.498 47.195 7.46 47.198 7.461 47.184 7.467 47.165 7.465 47.161 7.438 47.17 7.428 47.187 7.427 47.199 7.458 47.209 7.528 47.22 7.605 47.221"
 def poly2 = "7.427 47.158 7.672 47.158 7.672 47.223 7.427 47.223 7.427 47.158"
   
 def bbox1 = (7.427, 47.158, 7.672, 47.223)
 def bbox2 = (7.4, 47.1, 7.6, 47.2)
}