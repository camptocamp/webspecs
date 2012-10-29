package c2c.webspecs.suite
package prod

import c2c.webspecs.geonetwork.geocat.spec.WP10._
import c2c.webspecs.geonetwork.geocat.spec.WP12._
import c2c.webspecs.geonetwork.geocat.spec.WP15._
import c2c.webspecs.geonetwork.geocat.spec.WP16._
import c2c.webspecs.geonetwork.geocat.spec.WP1._
import c2c.webspecs.geonetwork.geocat.spec.WP3._
import c2c.webspecs.geonetwork.geocat.spec.WP4._
import c2c.webspecs.geonetwork.geocat.spec.WP5.csw.search._
import c2c.webspecs.geonetwork.geocat.spec.WP5.basic.search._
import c2c.webspecs.geonetwork.geocat.spec.WP6._
import c2c.webspecs.geonetwork.geocat.spec.WP7._
import c2c.webspecs.geonetwork.geocat.spec.WP9._
import c2c.webspecs.Properties
import java.text.SimpleDateFormat
import java.util.Date
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.runner.SpecificationsFinder
import org.specs2.specification.Step
import org.specs2.Specification
import scala.Option.option2Iterable
import org.specs2.main.Arguments

@RunWith(classOf[JUnitRunner])
class AllSpecs extends AbstractAllSpecs("Production Specifications: All Work Packages",
      classOf[WP1],
      classOf[WP3],
      classOf[WP5CswSearch] ,
	  classOf[WP6],
	  classOf[WP7],
      classOf[WP15], 
      classOf[WP16] )

@RunWith(classOf[JUnitRunner])
class WP1 extends AbstractAllSpecs("WP 1: Add CHE Schema",
      classOf[ImportCheMetadataSpec],
      classOf[ImportValidationSpec])

@RunWith(classOf[JUnitRunner])
class WP3 extends AbstractAllSpecs("WP 3: Shared Object (No UI)",
      classOf[AccessContactsSpec],
      classOf[AccessExtentsSpec],
      classOf[AccessFormatsSpec],
      classOf[AccessKeywordsSpec],
      classOf[AddSharedContactsSpec],
      // ? classOf[AddSharedExtentsSpec],
      classOf[AddSharedFormatSpec],
      classOf[AddSharedKeywordsSpec],
      classOf[ContactsMatchSpec],
      classOf[AccessSharedObjectHtmlListSpecExtentsSpec],
      classOf[UpdateXlinksCachingSpec],
      classOf[ImportSpecialExtentsSpec],
      classOf[EscapeSpecialCharsInUserSpec])

@RunWith(classOf[JUnitRunner])
class WP5CswSearch extends AbstractAllSpecs("WP 5: Csw Indexing and searching",
      classOf[DifferentLanguageCswSearchSpec],
      classOf[CswResetIndexReaderAfterImportSpec],
      classOf[SpatialCswSearchSpec],
      classOf[CswSearchOrderSpec])

@RunWith(classOf[JUnitRunner])
class WP5BasicSearch extends AbstractAllSpecs("WP 5: Basic Indexing and searching",
		classOf[DifferentLanguageBasicSearchSpec],
		classOf[BasicResetIndexReaderAfterImportSpec],
		classOf[BasicSearchOrderSpec])

@RunWith(classOf[JUnitRunner])
class WP6 extends AbstractAllSpecs("WP 6: Check CSW service",
      classOf[CswGetCapabilitiesServiceUrlSpec],
      classOf[CswLanguageSpec],
      classOf[CswOutputSchemaSpec],
      classOf[CswTransactionalXmlTestSpec],
      classOf[CswXmlTestSpec],
      classOf[CswDublinCoreUriSpec])

@RunWith(classOf[JUnitRunner])
class WP7 extends AbstractAllSpecs("WP 7: Search user interface",
      classOf[MefExportSpec],
      classOf[GeocatPagesLoadSpec]
      )

@RunWith(classOf[JUnitRunner])
class WP15 extends AbstractAllSpecs("WP 15: Metadata Edit",
      classOf[UpdateContactViaMetadataUpdate],
      classOf[KeywordsXlinkAddSpec],
      classOf[ExtentXlinkAddSpec],
      classOf[UpdateNonXlinkViaMetadataUpdate])

@RunWith(classOf[JUnitRunner])
class WP16 extends AbstractAllSpecs("WP 16: Misc. tests",
      classOf[MetadataValidationReportSpec],
      classOf[PreStyleSheetSpec],
      classOf[XmlInfoServiceLocalisationSpec])
