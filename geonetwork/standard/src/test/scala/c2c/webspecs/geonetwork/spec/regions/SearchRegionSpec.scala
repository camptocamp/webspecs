package c2c.webspecs
package geonetwork
package spec.regions

import csw._
import regions._
import org.specs2.specification.Step
import scala.xml.NodeSeq
import scala.xml.transform.RewriteRule
import scala.xml._
import org.apache.http.entity.mime.content.ByteArrayBody
import c2c.webspecs.geonetwork.spec.csw.search.CswGetRecordsSpec
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml.transform.RuleTransformer

@RunWith(classOf[JUnitRunner])
class SearchRegionSpec extends GeonetworkSpecification with AbstractRegionsSpec { def is =
  "This spec test XmlSearch and Csw search with a region declared" ^ Step(setup) ^ step(importMd) ^ sequential ^
      "Csw search with a within filter that has a region id should only find metadata within that region" ! cswBasicSearch ^
      "XmlSearch with a within filter that has a region id should only find metadata within that region" ! xmlBasicSearch ^
      "Csw search with a within filter that has 2 region ids should find metadata within both of those regions" ! cswBorderSearch ^
      "XmlSearch with a within filter that has 2 region ids should find metadata within both of those regions" ! xmlBorderSearch ^
      Step(tearDown)
      
  val bareMdFile = "/geonetwork/data/bare-iso19139.xml"
  val extentParentName = "MD_DataIdentification"
  val region1Id = regions1.head.id
  val region2Id = regions1.tail.head.id
  lazy val extents = Seq(Bern, SwissBorder, Paris)
  def createid(i: Int) = i+"-"+datestamp 
  val ids = 0 until extents.size map (createid)
  def importMd = {
    val bareMd = ResourceLoader.loadDataFromClassPath(bareMdFile, getClass, Map("{uuid}" -> "{uuid}"))._1
    val mds = extents.zipWithIndex.map{extent =>
      val md = XML.loadString(bareMd.replace("{uuid}", createid(extent._2)))
      new RuleTransformer(new AddExtent(extentParentName, extent._1))(md)
    }
    val ids = mds.map{md =>
      val contentBody = new ByteArrayBody(md.toString.getBytes("UTF-8"), "application/xml", "Data")
      ImportMetadata(contentBody, ImportStyleSheets.NONE, false, config.groupId, uuidAction = UuidAction.overwrite).execute().value
    }
    registerNewMd(ids:_*)
    ids
  }
  def cswBasicSearch = {
    val within = Within(region1Id)
    val idFilters = ids.tail.foldLeft(PropertyIsEqualTo("_uuid", ids.head): OgcFilter)((acc,next) => acc or PropertyIsEqualTo("_uuid", next))
    val value = CswGetRecordsRequest((within and idFilters).xml, ResultTypes.results, OutputSchemas.Record).execute().value
    val records = value.getXml \\ "Record"
    val foundIds = (records \ "identifier").toList.map(_.text)
    (foundIds must haveSize(1)) and
        (foundIds must containAllOf(List(ids.head)))
  }
  def xmlBasicSearch = {
    val idParams = ids.map(id => "_OR__uuid" -> id)
    val within = "relation" -> Within.toString.toLowerCase()
    val region = 'geometry -> ("region:"+region1Id)
    val records = XmlSearch().search(idParams :+ within :+ region :_*).execute().value
    val foundIds = records.records.map{_.uuid}
    (foundIds must haveSize(1)) and
        (foundIds must containAllOf(List(ids.head)))
  }
def cswBorderSearch = {
    val within = Within(region1Id+","+region2Id)
    val idFilters = ids.tail.foldLeft(PropertyIsEqualTo("_uuid", ids.head): OgcFilter)((acc,next) => acc or PropertyIsEqualTo("_uuid", next))
    val value = CswGetRecordsRequest((within and idFilters).xml, ResultTypes.results, OutputSchemas.Record).execute().value
    val records = value.getXml \\ "Record"
    val foundIds = (records \ "identifier").toList.map(_.text)
    (foundIds must haveSize(2)) and
        (foundIds must containAllOf(List(ids.head, ids.tail.head)))
}
def xmlBorderSearch = {
    val idParams = ids.map(id => "_OR__uuid" -> id)
            val within = "relation" -> Within.toString.toLowerCase()
    val region = 'geometry -> ("region:"+region1Id+","+region2Id)
    val records = XmlSearch().search(idParams :+ within :+ region :_*).execute().value
    val foundIds = records.records.map{_.uuid}
    (foundIds must haveSize(2)) and
        (foundIds must containAllOf(List(ids.head, ids.tail.head)))
}

  lazy val Bern =
<gmd:extent xmlns:che="http://www.geocat.ch/2008/che" xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:srv="http://www.isotc211.org/2005/srv" xmlns:gml="http://www.opengis.net/gml" xmlns:geonet="http://www.fao.org/geonetwork" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" gco:isoType="gmd:MD_Metadata">
  <gmd:EX_Extent>
    <gmd:description xsi:type="gmd:PT_FreeText_PropertyType">
      <gmd:PT_FreeText>
        <gmd:textGroup>
          <gmd:LocalisedCharacterString locale="#EN">Bern</gmd:LocalisedCharacterString>
        </gmd:textGroup>
      </gmd:PT_FreeText>
    </gmd:description>
    <gmd:geographicElement>
      <gmd:EX_BoundingPolygon>
        <gmd:extentTypeCode>
          <gco:Boolean>1</gco:Boolean>
        </gmd:extentTypeCode>
        <gmd:polygon>
          <gml:MultiSurface gml:id="N89ce40b59dbb4b7e896b2bbe80a23210">
            <gml:surfaceMember>
              <gml:Polygon gml:id="N89ce40b59dbb4b7e896b2bbe80a23210.1">
                <gml:exterior>
                  <gml:LinearRing>
                    <gml:posList>7.303 46.93 7.307 46.94 7.349 46.948 7.368 46.956 7.372 46.961 7.379 46.961 7.383 46.966 7.412 46.97 7.428 46.974 7.433 46.972 7.44 46.975 7.443 46.981 7.452 46.988 7.453 46.983 7.455 46.977 7.47 46.971 7.483 46.971 7.486 46.965 7.479 46.963 7.479 46.956 7.484 46.945 7.494 46.94 7.494 46.938 7.478 46.936 7.471 46.925 7.459 46.935 7.447 46.931 7.415 46.937 7.401 46.921 7.381 46.932 7.373 46.933 7.339 46.925 7.339 46.92 7.331 46.919 7.328 46.919 7.325 46.925 7.295 46.923 7.303 46.93</gml:posList>
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

  lazy val SwissBorder =
<gmd:extent xmlns:che="http://www.geocat.ch/2008/che" xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:srv="http://www.isotc211.org/2005/srv" xmlns:gml="http://www.opengis.net/gml" xmlns:geonet="http://www.fao.org/geonetwork" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" gco:isoType="gmd:MD_Metadata">
  <gmd:EX_Extent>
    <gmd:description xsi:type="gmd:PT_FreeText_PropertyType">
      <gmd:PT_FreeText>
        <gmd:textGroup>
          <gmd:LocalisedCharacterString locale="#EN">SwissBorder</gmd:LocalisedCharacterString>
        </gmd:textGroup>
      </gmd:PT_FreeText>
    </gmd:description>
    <gmd:geographicElement>
      <gmd:EX_BoundingPolygon>
        <gmd:extentTypeCode>
          <gco:Boolean>1</gco:Boolean>
        </gmd:extentTypeCode>
        <gmd:polygon>
          <gml:MultiSurface gml:id="N89ce40b59dbb4b7e896b2bbe80a23210">
            <gml:surfaceMember>
              <gml:Polygon gml:id="N89ce40b59dbb4b7e896b2bbe80a23210.1">
                <gml:exterior>
                  <gml:LinearRing>
                    <gml:posList>5.7643 46.882 6.6373 46.882 6.6373 46.3364 5.7643 46.3364 5.7643 46.882</gml:posList>
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
  
  lazy val Paris =
<gmd:extent xmlns:che="http://www.geocat.ch/2008/che" xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:srv="http://www.isotc211.org/2005/srv" xmlns:gml="http://www.opengis.net/gml" xmlns:geonet="http://www.fao.org/geonetwork" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" gco:isoType="gmd:MD_Metadata">
  <gmd:EX_Extent>
    <gmd:description xsi:type="gmd:PT_FreeText_PropertyType">
      <gmd:PT_FreeText>
        <gmd:textGroup>
          <gmd:LocalisedCharacterString locale="#EN">Stockholm</gmd:LocalisedCharacterString>
        </gmd:textGroup>
      </gmd:PT_FreeText>
    </gmd:description>
    <gmd:geographicElement>
      <gmd:EX_BoundingPolygon>
        <gmd:extentTypeCode>
          <gco:Boolean>1</gco:Boolean>
        </gmd:extentTypeCode>
        <gmd:polygon>
          <gml:MultiSurface gml:id="N89ce40b59dbb4b7e896b2bbe80a23210">
            <gml:surfaceMember>
              <gml:Polygon gml:id="N89ce40b59dbb4b7e896b2bbe80a23210.1">
                <gml:exterior>
                  <gml:LinearRing>
                    <gml:posList>18.07 59.33 18.07 59.3 18 59.3 18 59.33 18.07 59.33</gml:posList>
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

class AddExtent(extentParentName:String, extent:Node) extends RewriteRule {
  override def transform(n: Node): Seq[Node] = n match {
    case e:Elem if e.label == extentParentName => e.copy(child= (e.child :+ extent))
    case n => n
  }
}

