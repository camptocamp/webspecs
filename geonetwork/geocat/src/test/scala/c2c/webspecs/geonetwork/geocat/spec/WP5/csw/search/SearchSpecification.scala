package c2c.webspecs
package geonetwork
package geocat
package spec.WP5.csw.search
import c2c.webspecs.geonetwork.geocat.spec.WP3.ProcessImportedMetadataSpec
import java.util.Date
import scala.xml.NodeSeq
import org.specs2.execute.Result

trait SearchSpecification extends GeocatSpecification with c2c.webspecs.geonetwork.spec.csw.search.SearchSpecification {
  override def pathToSearchMetadata =
    "/geocat/data/csw/search/"

  override def importExtraMd(numberOfRecords: Int, md: String = "/geocat/data/bare.iso19139.che.xml", identifier: String, styleSheet: ImportStyleSheets.ImportStyleSheet = ImportStyleSheets.NONE) =
    importMd(numberOfRecords, md, identifier, styleSheet)
}