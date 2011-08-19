package c2c.webspecs.suite

import org.specs2.SpecificationWithJUnit
import org.specs2.runner.SpecificationsFinder
import c2c.webspecs.geonetwork.geocat.spec.WP1._
import c2c.webspecs.geonetwork.geocat.spec.WP2._
import c2c.webspecs.geonetwork.geocat.spec.WP3._
import c2c.webspecs.geonetwork.geocat.spec.WP4._
import c2c.webspecs.geonetwork.geocat.spec.WP5._
import c2c.webspecs.geonetwork.geocat.spec.WP6._
import c2c.webspecs.geonetwork.geocat.spec.WP16._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import java.text.SimpleDateFormat
import java.util.Date


@RunWith(classOf[JUnitRunner]) 
class AllSpecs extends SpecificationWithJUnit with SpecificationsFinder { def is =
    examplesLinks("All Work Packages - "+dateTime)

    def examplesLinks(t: String) = {
  val specs = List(
      classOf[WP1],
//      classOf[WP2],     
      classOf[WP3],
	  classOf[WP4],
	  classOf[WP5],
	  classOf[WP6],
	  classOf[WP16]	  
	).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

    def dateTime = {
        val dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        val date = new Date();
        dateFormat.format(date);
    }
}



class WP1 extends SpecificationWithJUnit with SpecificationsFinder { def is =

    examplesLinks("WP 1: Add CHE Schema")

    def examplesLinks(t: String) = {
  val specs = List(
        classOf[ImportCheMetadataSpec]
      ).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

}
class WP2 extends SpecificationWithJUnit with SpecificationsFinder { def is =

    examplesLinks("WP 2: Compares every metadatas from old version against the new one")

    def examplesLinks(t: String) = {
  val specs = List(
        classOf[CompareGeocat1Metadata]
      ).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

}

class WP3 extends SpecificationWithJUnit with SpecificationsFinder { def is =

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
        classOf[AddXLinksSpec],
        classOf[ImportSpecialExtentsSpec],
        classOf[ProcessImportedMetadataSpec],
        classOf[ValidateSharedObjectSpec]
      ).flatMap{s => createSpecification(s.getName)}
      specs.
        foldLeft(t.title) { (res, cur) => res ^ link(cur) }
    }

}

class WP4 extends SpecificationWithJUnit with SpecificationsFinder { def is =

	examplesLinks("WP 4: Resolve XLinks")

	def examplesLinks(t: String) = {
		val specs = List(
				classOf[TestXlinksUpdate]
				).flatMap{s => createSpecification(s.getName)}
		specs.
		foldLeft(t.title) { (res, cur) => res ^ link(cur) }
	}
}

class WP5 extends SpecificationWithJUnit with SpecificationsFinder { def is =

examplesLinks("WP 5: Indexing and searching")

def examplesLinks(t: String) = {
	val specs = List(
			classOf[NonSpatialSearchQuerySpec],
			classOf[SpatialSearchSpec]
			).flatMap{s => createSpecification(s.getName)}
	specs.
	foldLeft(t.title) { (res, cur) => res ^ link(cur) }
}
}

class WP6 extends SpecificationWithJUnit with SpecificationsFinder { def is =

	examplesLinks("WP 6: Check CSW service")

	def examplesLinks(t: String) = {
		val specs = List(
				classOf[CswGetCapabilitiesServiceUrlSpec],
				classOf[CswLanguageSpec],
				classOf[CswOutputSchemaSpec]
				).flatMap{s => createSpecification(s.getName)}
		specs.
		foldLeft(t.title) { (res, cur) => res ^ link(cur) }
	}
}

class WP16 extends SpecificationWithJUnit with SpecificationsFinder { def is =

	examplesLinks("WP 16: Misc. tests")

	def examplesLinks(t: String) = {
		val specs = List(
		    	classOf[MetadataValidationReportSpec],
				classOf[MonitoringSpec],
				classOf[PreStyleSheetSpec],
				classOf[RegisterXslSpec],
				classOf[TestMetadataExpiredServicesSpec]
				).flatMap{s => createSpecification(s.getName)}
		specs.
		foldLeft(t.title) { (res, cur) => res ^ link(cur) }
	}
}

