package c2c.webspecs
package geonetwork
package geocat
package spec.WP5.csw.search

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CswResetIndexReaderAfterImportSpec 
	extends c2c.webspecs.geonetwork.spec.csw.search.CswResetIndexReaderAfterImportSpec 
	with SearchSpecification