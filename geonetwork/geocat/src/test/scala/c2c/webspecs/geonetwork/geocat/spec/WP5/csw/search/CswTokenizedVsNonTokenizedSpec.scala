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
class CswTokenizedVsNonTokenizedSpec 
	extends c2c.webspecs.geonetwork.spec.csw.search.CswTokenizedVsNonTokenizedSpec 
	with SearchSpecification {
  
  override def importExtraMd(numberOfRecords: Int, md: String = "/geocat/data/TokenizedVsNonTokenizedData.xml", identifier: String, styleSheet: ImportStyleSheets.ImportStyleSheet = ImportStyleSheets.NONE) =
    importMd(numberOfRecords, md, identifier, styleSheet)
}