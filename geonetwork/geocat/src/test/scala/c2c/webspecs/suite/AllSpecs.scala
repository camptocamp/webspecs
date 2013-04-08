package c2c.webspecs.suite

import org.specs2.Specification
import org.specs2.runner.SpecificationsFinder
import c2c.webspecs.geonetwork.geocat.spec.WP1._
import c2c.webspecs.geonetwork.geocat.spec.WP2._
import c2c.webspecs.geonetwork.geocat.spec.WP3._
import c2c.webspecs.geonetwork.geocat.spec.WP4._
import c2c.webspecs.geonetwork.geocat.spec.WP5.csw.search._
import c2c.webspecs.geonetwork.geocat.spec.WP5.basic.search._
import c2c.webspecs.geonetwork.geocat.spec.WP6._
import c2c.webspecs.geonetwork.geocat.spec.WP7._
import c2c.webspecs.geonetwork.geocat.spec.WP9._
import c2c.webspecs.geonetwork.geocat.spec.WP10._
import c2c.webspecs.geonetwork.geocat.spec.WP12._
import c2c.webspecs.geonetwork.geocat.spec.WP15._
import c2c.webspecs.geonetwork.geocat.spec.WP16._
import c2c.webspecs.geonetwork.geocat.spec.RE2012_ExtendedCategories._
import c2c.webspecs.geonetwork.geocat.spec.bugs._

import java.text.SimpleDateFormat
import java.util.Date
import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import c2c.webspecs.Properties
import org.specs2.main.Arguments

@RunWith(classOf[JUnitRunner])
class AllSpecs extends AbstractAllSpecs("All Work Packages", classOf[WP1],
//      classOf[WP2],     
      classOf[WP3],
	  classOf[WP4],
	  classOf[WP5CswSearch],
	  classOf[WP5BasicSearch],
	  classOf[WP6],
	  classOf[WP7],
	  classOf[WP9],
      classOf[WP10],
      classOf[WP11_12],
      classOf[WP15], 
      classOf[WP16],
      classOf[RE2012_ExtendedCategories],
      classOf[BugFixes]
      )


@RunWith(classOf[JUnitRunner])
class WP1 extends AbstractAllSpecs("WP 1: Add CHE Schema",
        classOf[ImportCheMetadataSpec],
        classOf[ImportPTFreeTextSpec],
        classOf[CanEditTemplate],
        classOf[ImportValidationSpec])

@RunWith(classOf[JUnitRunner])
class WP2 extends AbstractAllSpecs("WP 2: Compares every metadatas from old version against the new one",
        classOf[CompareGeocat1Metadata])

@RunWith(classOf[JUnitRunner])
class WP3 extends AbstractAllSpecs("WP 3: Shared Object (No UI)",
        classOf[AccessContactsSpec],
        classOf[AccessExtentsSpec],
        classOf[AccessFormatsSpec],
        classOf[AccessKeywordsSpec],
        classOf[AddSharedContactsSpec],
        classOf[AddSharedExtentsSpec],
        classOf[AddSharedFormatSpec],
        classOf[AddSharedKeywordsSpec],
        classOf[ContactsMatchSpec],
        classOf[AccessSharedObjectHtmlListSpecExtentsSpec],
        classOf[UpdateXlinksCachingSpec],
        classOf[AddXLinksSpec],
        classOf[ImportSpecialExtentsSpec],
        classOf[ProcessImportedMetadataSpec],
        classOf[DeleteValidatedSharedObjectSpec],
        classOf[EscapeSpecialCharsInUserSpec],
        classOf[ValidateSharedObjectSpec])

@RunWith(classOf[JUnitRunner])
class WP4 extends AbstractAllSpecs("WP 4: Resolve XLinks",
				classOf[TestXlinksUpdate])

@RunWith(classOf[JUnitRunner])
class WP5CswSearch extends AbstractAllSpecs("WP 5: Csw Indexing and searching",
			classOf[NonSpatialCswSearchQuerySpec],
			classOf[PagingCswSearchSpec],
			classOf[DifferentLanguageCswSearchSpec],
			classOf[CswResetIndexReaderAfterImportSpec],
			classOf[SpatialCswSearchSpec],
			classOf[CswTokenizedVsNonTokenizedSpec],
			classOf[CswSearchOrderSpec])

@RunWith(classOf[JUnitRunner])
class WP5BasicSearch extends AbstractAllSpecs("WP 5: Basic Indexing and searching",
			classOf[NonSpatialBasicSearchQuerySpec],
			classOf[PagingBasicSearchSpec],
			classOf[DifferentLanguageBasicSearchSpec],
			classOf[BasicResetIndexReaderAfterImportSpec],
			classOf[BasicTokenizedVsNonTokenizedSpec],
			classOf[BasicSearchOrderSpec])

@RunWith(classOf[JUnitRunner])
class WP6 extends AbstractAllSpecs("WP 6: Check CSW service",
				classOf[CswGetCapabilitiesServiceUrlSpec],
				classOf[CswLanguageSpec],
				classOf[CswOutputSchemaSpec],
				classOf[CswTransactionalXmlTestSpec],
				classOf[CswXmlTestSpec],
				// TODO enable when summary is more reliable classOf[SummaryAccuracySpec],
				classOf[CswDublinCoreUriSpec],
				classOf[CswTransactionSpec])

@RunWith(classOf[JUnitRunner])
class WP7 extends AbstractAllSpecs("WP 7: Search user interface",
			classOf[MefExportSpec],
			classOf[MefImportSpec],
      classOf[GeocatPagesLoadSpec])

@RunWith(classOf[JUnitRunner])
class WP9 extends AbstractAllSpecs("WP 9: Metadata Viewer",
      classOf[MetadataShowSpec])

@RunWith(classOf[JUnitRunner]) 
class WP10 extends AbstractAllSpecs("WP 10: Reusable Object UI",
		    	classOf[ReusableNonValidatedListSpec],
		    	classOf[RejectSharedObjectSpec],
		    	classOf[ReusableReferencesGiveOwner],
		    	classOf[DeletedNonValidatedSharedObjectSpec])

@RunWith(classOf[JUnitRunner]) 
class WP11_12 extends AbstractAllSpecs("WP 11,12: GM03 Import and Export",
      classOf[GM03V1Spec],
      classOf[GM03V2Spec])

@RunWith(classOf[JUnitRunner])
class WP15 extends AbstractAllSpecs("WP 15: Metadata Edit",
                classOf[UpdateContactViaMetadataUpdate],
                classOf[KeywordsXlinkAddSpec],
                classOf[ExtentXlinkAddSpec],
                classOf[LocaleFixedInfoSpec],
                classOf[SameXlinkUpdatedBugSpec],
                classOf[UpdateNonXlinkViaMetadataUpdate])

@RunWith(classOf[JUnitRunner])
class WP16 extends AbstractAllSpecs("WP 16: Misc. tests",
            classOf[MetadataValidationReportSpec],
            classOf[GeoportalSpec],
            classOf[MonitoringSpec],
            classOf[PreStyleSheetSpec],
            classOf[RegisterXslSpec],
            classOf[TestMetadataExpiredServicesSpec])

@RunWith(classOf[JUnitRunner])
class RE2012_ExtendedCategories extends AbstractAllSpecs("RE2012_ExtendedCategories",
            classOf[ExportSpec],
            classOf[UpdateInfoSpec])

@RunWith(classOf[JUnitRunner])
class BugFixes extends AbstractAllSpecs("Bug Fixes",
//            classOf[AddRemoveOverviewSpec],
            classOf[ClickableAttributeLinkSpec]
            )
