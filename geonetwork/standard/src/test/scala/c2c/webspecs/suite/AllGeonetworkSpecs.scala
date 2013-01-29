package c2c.webspecs.suite

import c2c.webspecs.geonetwork.spec._
import csw._
import csw.search._
import edit._
import formatter._
import importmetadata._
import regions._
import get._
import c2c.webspecs.geonetwork.spec.search._
import c2c.webspecs.geonetwork.spec.search.oldsearch._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AllGeonetworkSpecs
  extends AbstractAllSpecs("AllGeonetworkSpecs",
    classOf[CswSpecs],
    classOf[EditingSpecs],
    classOf[AccessSpecs],
    classOf[FormatterSpecs],
    classOf[ImportSpecs],
    classOf[SearchSpecs],
    classOf[RegionSpecs],
    classOf[OldSearchSpecs])

class CswSpecs
  extends AbstractAllSpecs("CswSpecs",
    classOf[CswGetCapabilitiesServiceUrlSpec],
    classOf[CswGetRecordsByIdSpec],
    classOf[CswLanguageSpec],
//    classOf[CswTransactionUpdateSpec],  // random failures fails
    classOf[CswXmlTestSpec],
    classOf[CswOutputSchemaSpec],
    classOf[CswVirtualEndPointSpec],
    // searching
    classOf[CswGetRecordsSpec],
    classOf[CswResetIndexReaderAfterImportSpec],
    classOf[CswSearchOrderSpec],
    classOf[DifferentLanguageCswSearchSpec],
    classOf[NonSpatialCswSearchQuerySpec],
    classOf[CswTokenizedVsNonTokenizedSpec],
    classOf[PagingCswSearchSpec])

class EditingSpecs
  extends AbstractAllSpecs("EditingSpecs")

class AccessSpecs
extends AbstractAllSpecs("GetAccessSpecs",
    classOf[MetadataShowSpec])

class FormatterSpecs
  extends AbstractAllSpecs("FormatterSpecs",
    classOf[RegisterFormatterSpec])

class RegionSpecs
extends AbstractAllSpecs("RegionSpecs",
        classOf[GetRegionGeomSpec],
        classOf[GetRegionSpec],
        classOf[ListRegionsSpec],
        classOf[SearchRegionSpec],
        classOf[GetRegionMapSpec]
        )

class ImportSpecs
  extends AbstractAllSpecs("ImportSpecs",
    classOf[AddSampleDataSpec],
    classOf[ImportMetadataSpec]
    )

@RunWith(classOf[JUnitRunner])
class SearchSpecs
extends AbstractAllSpecs("SearchSpecs",
		classOf[BasicSearchResetIndexReaderAfterImportSpec],
		classOf[BasicXmlSearchSpec],
		classOf[BasicSearchOrderSpec],
		classOf[NonSpatialBasicSearchQuerySpec],
		classOf[PagingBasicSearchSpec],
		classOf[BasicTokenizedVsNonTokenizedSpec],
		classOf[DifferentLanguageBasicSearchSpec],
		classOf[SelectAllBugSpec],
		classOf[SummaryAccuracySpec],
		classOf[IndexMdWithNoLangSpec],
		classOf[BasicSpatialSearchSpec]
		)
@RunWith(classOf[JUnitRunner])
class OldSearchSpecs
extends AbstractAllSpecs("OldSearchSpecs",
        classOf[OldSearchResetIndexReaderAfterImportSpec],
        classOf[OldSearchOrderSpec],
        classOf[NonSpatialOldSearchQuerySpec],
        classOf[PagingOldSearchSpec],
        classOf[OldTokenizedVsNonTokenizedSpec],
        classOf[DifferentLanguageOldSearchSpec]
        )
