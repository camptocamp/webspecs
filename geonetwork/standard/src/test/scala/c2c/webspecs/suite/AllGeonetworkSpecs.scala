package c2c.webspecs.suite

import c2c.webspecs.geonetwork.spec._
import csw._
import csw.search._
import edit._
import formatter._
import importmetadata._
import c2c.webspecs.geonetwork.spec.search._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AllGeonetworkSpecs
  extends AbstractAllSpecs("AllGeonetworkSpecs",
    classOf[CswSpecs],
    classOf[EditingSpecs],
    classOf[FormatterSpecs],
    classOf[ImportSpecs],
    classOf[SearchSpecs])

class CswSpecs
  extends AbstractAllSpecs("CswSpecs",
    classOf[CswGetCapabilitiesServiceUrlSpec],
    classOf[CswGetRecordsSpec],
    classOf[CswGetRecordsByIdSpec],
    classOf[CswLanguageSpec],
    classOf[CswTransactionUpdateSpec],
    classOf[CswXmlTestSpec],
    classOf[CswOutputSchemaSpec],
    classOf[CswResetIndexReaderAfterImportSpec],
    classOf[CswSearchOrderSpec],
    classOf[DifferentLanguageCswSearchSpec],
    classOf[NonSpatialCswSearchQuerySpec],
    classOf[PagingCswSearchSpec])

class EditingSpecs
  extends AbstractAllSpecs("EditingSpecs")

class FormatterSpecs
  extends AbstractAllSpecs("FormatterSpecs",
    classOf[RegisterFormatterSpec])

class ImportSpecs
  extends AbstractAllSpecs("ImportSpecs",
    classOf[AddSampleDataSpec],
    classOf[ImportMetadataSpec]
    )

class SearchSpecs
extends AbstractAllSpecs("SearchSpecs",
		classOf[BasicSearchResetIndexReaderAfterImportSpec],
		classOf[BasicXmlSearchSpec],
		classOf[BasicSearchOrderSpec],
		classOf[NonSpatialBasicSearchQuerySpec],
		classOf[DifferentLanguageBasicSearchSpec]
		)
