package c2c.webspecs.suite

import org.specs2.Specification
import org.specs2.runner.SpecificationsFinder
import c2c.webspecs.geonetwork.geocat.spec.WP1._
import c2c.webspecs.geonetwork.geocat.spec.WP2._
import c2c.webspecs.geonetwork.geocat.spec.WP3._
import c2c.webspecs.geonetwork.geocat.spec.WP4._
import c2c.webspecs.geonetwork.geocat.spec.WP5._
import c2c.webspecs.geonetwork.geocat.spec.WP6._
import c2c.webspecs.geonetwork.geocat.spec.WP7._
import c2c.webspecs.geonetwork.geocat.spec.WP9._
import c2c.webspecs.geonetwork.geocat.spec.WP10._
import c2c.webspecs.geonetwork.geocat.spec.WP12._
import c2c.webspecs.geonetwork.geocat.spec.WP15._
import c2c.webspecs.geonetwork.geocat.spec.WP16._
import java.text.SimpleDateFormat
import java.util.Date
import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import c2c.webspecs.Properties


@RunWith(classOf[JUnitRunner])
class AllSpecs extends Specification with SpecificationsFinder { 
  Properties.classLoader=classOf[AllSpecs].getClassLoader()
  
  def is =
    examplesLinks("All Work Packages - "+dateTime)

    def examplesLinks(t: String) = {
  val specs = List(
      classOf[WP1],
//      classOf[WP2],     
      classOf[WP3],
	  classOf[WP4],
	  classOf[WP5],
	  classOf[WP6],
	  classOf[WP7],
	  classOf[WP9],
      classOf[WP10],
      classOf[WP11_12],
      classOf[WP15], 
      classOf[WP16]  
	).flatMap{s => createSpecification(s.getName)}
      specs.foldLeft(initVal(t)) { (res, cur) => res ^ link(cur) }
    }

    def initVal(t:String) = t.title ^ sequential ^ Step(() => Thread.sleep(2000))

    def dateTime = {
        val dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        val date = new Date();
        dateFormat.format(date);
    }
}



@RunWith(classOf[JUnitRunner])
class WP1 extends Specification with SpecificationsFinder { def is =

    examplesLinks("WP 1: Add CHE Schema")

    def examplesLinks(t: String) = {
  val specs = List(
        classOf[ImportCheMetadataSpec]
      ).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

}
@RunWith(classOf[JUnitRunner])
class WP2 extends Specification with SpecificationsFinder { def is =

    examplesLinks("WP 2: Compares every metadatas from old version against the new one")

    def examplesLinks(t: String) = {
  val specs = List(
        classOf[CompareGeocat1Metadata]
      ).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

}
@RunWith(classOf[JUnitRunner])
class WP3 extends Specification with SpecificationsFinder { def is =

    examplesLinks("WP 3: Shared Object (No UI)")

    def examplesLinks(t: String) = {
  val specs = List(
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
        classOf[EscapeSpecialCharsInUserSpec],
        classOf[ValidateSharedObjectSpec]
      ).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

}

@RunWith(classOf[JUnitRunner])
class WP4 extends Specification with SpecificationsFinder { def is =

	examplesLinks("WP 4: Resolve XLinks")

	def examplesLinks(t: String) = {
		val specs = List(
				classOf[TestXlinksUpdate]
				).flatMap{s => createSpecification(s.getName)}
		specs.
		foldLeft(t.title) { (res, cur) => res ^ link(cur) }
	}
}

@RunWith(classOf[JUnitRunner])
class WP5 extends Specification with SpecificationsFinder { def is =

examplesLinks("WP 5: Indexing and searching")

def examplesLinks(t: String) = {
	val specs = List(
			classOf[NonSpatialSearchQuerySpec],
			classOf[PagingSearchSpec],
			classOf[DifferentLanguageSearchSpec],
			classOf[CswResetIndexReaderAfterImportSpec],
			classOf[SpatialSearchSpec],
			classOf[SearchOrderSpec]
			).flatMap{s => createSpecification(s.getName)}
	specs.
	foldLeft(t.title) { (res, cur) => res ^ link(cur) }
}
}

@RunWith(classOf[JUnitRunner])
class WP6 extends Specification with SpecificationsFinder { def is =

	examplesLinks("WP 6: Check CSW service")

	def examplesLinks(t: String) = {
		val specs = List(
				classOf[CswGetCapabilitiesServiceUrlSpec],
				classOf[CswLanguageSpec],
				classOf[CswOutputSchemaSpec],
				classOf[CswTransactionalXmlTestSpec],
				classOf[CswXmlTestSpec],
				classOf[SummaryAccuracySpec],
				classOf[CswDublinCoreUriSpec],
				classOf[CswTransactionSpec]
				).flatMap{s => createSpecification(s.getName)}
		specs.
		foldLeft(t.title) { (res, cur) => res ^ link(cur) }
	}
}
@RunWith(classOf[JUnitRunner])
class WP7 extends Specification with SpecificationsFinder { def is =

examplesLinks("WP 7: Search user interface")

def examplesLinks(t: String) = {
	val specs = List(
			classOf[MefExportSpec],
			classOf[MefImportSpec]
			).flatMap{s => createSpecification(s.getName)}
	specs.
	foldLeft(t.title) { (res, cur) => res ^ link(cur) }
}
}
@RunWith(classOf[JUnitRunner])
class WP9 extends Specification with SpecificationsFinder {
  def is =

    examplesLinks("WP 9: Metadata Viewer")

  def examplesLinks(t: String) = {
    val specs = List(
      classOf[MetadataShowSpec]).flatMap { s => createSpecification(s.getName) }
    specs.
      foldLeft(t.title) { (res, cur) => res ^ link(cur) }
  }
}

@RunWith(classOf[JUnitRunner]) 
class WP10 extends Specification with SpecificationsFinder { def is =

	examplesLinks("WP 10: Reusable Object UI")

	def examplesLinks(t: String) = {
		val specs = List(
		    	classOf[ReusableNonValidatedListSpec],
		    	classOf[RejectSharedObjectSpec],
		    	classOf[DeletedNonValidatedSharedObjectSpec]
				).flatMap{s => createSpecification(s.getName)}
		specs.
		foldLeft(t.title) { (res, cur) => res ^ link(cur) }
	}
}
@RunWith(classOf[JUnitRunner]) 
class WP11_12 extends Specification with SpecificationsFinder { def is =

examplesLinks("WP 11/12: GM03 Import and Export")

def examplesLinks(t: String) = {
    val specs = List(
      classOf[GM03V1Spec],
      classOf[GM03V2Spec]).flatMap { s => createSpecification(s.getName) }
    specs.
      foldLeft(t.title) { (res, cur) => res ^ link(cur) }
  }
}
@RunWith(classOf[JUnitRunner])
class WP15 extends Specification with SpecificationsFinder {
    def is =
            examplesLinks("WP 15: Metadata Edit")
            
            def examplesLinks(t: String) = {
        val specs = List(
                classOf[UpdateContactViaMetadataUpdate],
                classOf[KeywordsXlinkAddSpec],
                classOf[ExtentXlinkAddSpec],
                classOf[SameXlinkUpdatedBugSpec],
                classOf[UpdateNonXlinkViaMetadataUpdate]).flatMap { s => createSpecification(s.getName) }
        specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }
}
@RunWith(classOf[JUnitRunner])
class WP16 extends Specification with SpecificationsFinder { def is =

examplesLinks("WP 16: Misc. tests")

def examplesLinks(t: String) = {
    val specs = List(
            classOf[MetadataValidationReportSpec],
            classOf[GeoportalSpec],
            classOf[MonitoringSpec],
            classOf[PreStyleSheetSpec],
            classOf[XmlInfoServiceLocalisationSpec],
            classOf[GroupNameLocalizationSpec],
            classOf[RegisterXslSpec],
            classOf[TestMetadataExpiredServicesSpec]
            ).flatMap{s => createSpecification(s.getName)}
    specs.foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }
}
