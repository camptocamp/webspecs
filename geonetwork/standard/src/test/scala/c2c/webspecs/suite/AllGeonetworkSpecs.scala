package c2c.webspecs.suite

import c2c.webspecs.geonetwork.spec._
import csw._
import csw.search._
import edit._
import formatter._
import importmetadata._
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
    classOf[OldSearchSpecs])

class CswSpecs
  extends AbstractAllSpecs("CswSpecs",
    classOf[CswGetCapabilitiesServiceUrlSpec],
    classOf[CswGetRecordsByIdSpec],
    classOf[CswLanguageSpec],
    classOf[CswTransactionUpdateSpec],
    classOf[CswXmlTestSpec],
    classOf[CswOutputSchemaSpec],
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
		classOf[IndexMdWithNoLangSpec]
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
