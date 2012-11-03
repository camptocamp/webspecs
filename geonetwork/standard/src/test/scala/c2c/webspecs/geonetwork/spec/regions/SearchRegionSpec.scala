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

class SearchRegionSpec extends GeonetworkSpecification with AbstractRegionsSpec { def is =
  "This spec test XmlSearch and Csw search with a region declared" ^ Step(setup) ^ step(importMd) ^
      "Csw search with a within filter that has a region id should only find metadata within that region" ! cswSearch ^
      "XmlSearch with a within filter that has a region id should only find metadata within that region" ! xmlSearch ^
      Step(tearDown)
      
  val bareMdFile = "/geonetwork/data/bare-iso19139.xml"
  val extentParentName = "gmd:MD_DataIdentification"
  val regionId = regions1.head.categoryId+":"+regions1.head.id
  val extents = Seq(Bern, Paris)
  def createid(i: Int) = i+"-"+datestamp 
  val ids = 0 to extents.size map (createid)
  def importMd = {
    val bareMd = ResourceLoader.loadDataFromClassPath(bareMdFile, getClass, Map("{{uuid}}" -> "{{uuid}}"))._1
    val mds = extents.zipWithIndex.map{extent =>
      val md = XML.loadString(bareMd.replace("{{uuid}}", createid(extent._2)))
      new AddExtent(extentParentName, extent._1)(md)
    }
    val ids = mds.map{md =>
      val contentBody = new ByteArrayBody(md.toString.getBytes("UTF-8"), "application/xml", "Data")
      ImportMetadata(contentBody, ImportStyleSheets.NONE, false, config.groupId, uuidAction = UuidAction.overwrite).execute().value
    }
    registerNewMd(ids:_*)
    ids
  }
  def cswSearch = {
    val within = Within(regionId)
    val idFilters = ids.tail.foldLeft(PropertyIsEqualTo("_id", ids.head): OgcFilter)((acc,next) => acc or PropertyIsEqualTo("_id", next))
    val value = CswGetRecordsRequest((within and idFilters).xml, ResultTypes.results).execute().value
    val foundIds = (value.getXml \\ "Record" \ "identifier").toList.map(_.text)
    foundIds must_== List(ids.head)
  }
  def xmlSearch = {
    val idParams = ids.map(id => "_id" -> id)
    val within = "relation" -> "within"
    val region = 'region -> regionId
    val records = XmlSearch().search(idParams :+ within :+ region :_*).execute().value
    val foundIds = records.records.map{_.uuid}
    foundIds must_== List(ids.head)
  }

  val Bern = <extent/>
  val Paris = <extent/>  
}

class AddExtent(extentParentName:String, extent:Node) extends RewriteRule {
  override def transform(n: Node): Seq[Node] = n match {
    case e:Elem if e.label == extentParentName => e.copy(child= (e.child :+ extent))
    case n => n
  }
}

