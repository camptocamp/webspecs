package c2c.webspecs
package geonetwork
package spec.search

import org.specs2.specification.Step
import c2c.webspecs.geonetwork.regions._
import c2c.webspecs.geonetwork.spec.regions.AbstractRegionsSpec
import scala.xml._
import scala.xml.transform.RewriteRule
import scala.xml.transform.RuleTransformer
import org.apache.http.entity.mime.content.StringBody
import java.nio.charset.Charset
import java.io.ByteArrayInputStream
import org.apache.http.entity.mime.content.InputStreamBody

trait AbstractSpatialSearchSpec extends AbstractRegionsSpec {
  self: GeonetworkSpecification =>
  
 protected def titleExtension:String
	  
  def is = (getClass.getSimpleName+titleExtension).title ^ sequential ^
    "Test spatial searching" ^ Step(setup) ^ Step(importMd) ^
      "Search within 1 region should get the one metadata within that region" ! within1 ^
      "Search within 1 region should get the one metadata within that region" ! within2 ^
                Step(tearDown)
      
  protected def bareMd = "/geonetwork/data/bare-iso19139.xml"
 protected def extentParentNodeLabel = "MD_DataIdentification"
 
  private lazy val importMd = {
    val rawXml = XML.loadString(ResourceLoader.loadDataFromClassPath(bareMd, getClass(), uuid)._1)
    
    (regions1 ++ regions2).map {r =>
      val gml = GmlGetRegionGeomRequest(r.id).execute().value.getXml
      val metadata = new RuleTransformer(new AddExtent(gml))(rawXml)
      val id = ImportMetadata(
          new InputStreamBody(new ByteArrayInputStream(metadata.toString.getBytes("UTF-8")), "application/xml", uuid.toString+".xml"),
          ImportStyleSheets.NONE,
          false,
          config.groupId,
          UuidAction.generateUUID,
          ImportMdFileType.single,
          false).execute().value.id
      registerNewMd(Id(id))
      id
    }
  }

  def search(relation:SpatialRelation, regions:Region*):Int
  def within1 = {
    val results = search(Within, regions1.head)
    results must_== 1
  }
  def within2 = {
    val results = search(Within, regions1:_*)
    results must_== regions1.size
  }
 private class AddExtent(gml:NodeSeq) extends RewriteRule {
    def extent = 
      <gmd:extent>
         <gmd:EX_Extent>
           <gmd:geographicElement>
             <gmd:EX_BoundingPolygon><gmd:polygon>{gml}</gmd:polygon></gmd:EX_BoundingPolygon>
           </gmd:geographicElement>
         </gmd:EX_Extent>
      </gmd:extent>
   override def transform(n: Node): Seq[Node] = n match {
     case n: Elem if n.label == extentParentNodeLabel => n.copy(child = n.child ++ extent)
     case _ => n
   }
 }
}