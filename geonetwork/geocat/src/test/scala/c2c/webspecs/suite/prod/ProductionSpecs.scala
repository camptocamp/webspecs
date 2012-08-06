package c2c.webspecs.suite.prod

import c2c.webspecs.geonetwork.geocat.spec.WP10._
import c2c.webspecs.geonetwork.geocat.spec.WP12._
import c2c.webspecs.geonetwork.geocat.spec.WP15._
import c2c.webspecs.geonetwork.geocat.spec.WP16._
import c2c.webspecs.geonetwork.geocat.spec.WP1._
import c2c.webspecs.geonetwork.geocat.spec.WP3._
import c2c.webspecs.geonetwork.geocat.spec.WP4._
import c2c.webspecs.geonetwork.geocat.spec.WP5._
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
class AllSpecs extends Specification with SpecificationsFinder {
  Properties.classLoader = classOf[AllSpecs].getClassLoader()

  def is =
    examplesLinks("All Work Packages - " + dateTime)

  def examplesLinks(t: String) = {
    val specs = List(
      classOf[WP1],
      classOf[WP3],
      classOf[WP5] ,
	  classOf[WP6],
	  classOf[WP7],
      classOf[WP15], 
      classOf[WP16] ).flatMap { s => createSpecification(s.getName)(Arguments()) }
    specs.foldLeft(initVal(t)) { (res, cur) => res ^ link(cur) }
  }

  def initVal(t: String) = t.title ^ sequential ^ Step(() => Thread.sleep(2000))

  def dateTime = {
    val dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    val date = new Date();
    dateFormat.format(date);
  }
}

@RunWith(classOf[JUnitRunner])
class WP1 extends Specification with SpecificationsFinder {
  def is =

    examplesLinks("WP 1: Add CHE Schema")

  def examplesLinks(t: String) = {
    val specs = List(
      classOf[ImportCheMetadataSpec],
      classOf[ImportValidationSpec]).flatMap { s => createSpecification(s.getName)(Arguments()) }
    specs.
      foldLeft(t.title) { (res, cur) => res ^ link(cur) }
  }

}
@RunWith(classOf[JUnitRunner])
class WP3 extends Specification with SpecificationsFinder {
  def is =

    examplesLinks("WP 3: Shared Object (No UI)")

  def examplesLinks(t: String) = {
    val specs = List(
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
      classOf[EscapeSpecialCharsInUserSpec]).flatMap { s => createSpecification(s.getName)(Arguments()) }
    specs.
      foldLeft(t.title) { (res, cur) => res ^ link(cur) }
  }

}

@RunWith(classOf[JUnitRunner])
class WP5 extends Specification with SpecificationsFinder {
  def is =

    examplesLinks("WP 5: Indexing and searching")

  def examplesLinks(t: String) = {
    val specs = List(
      classOf[DifferentLanguageSearchSpec],
      classOf[CswResetIndexReaderAfterImportSpec],
      classOf[SpatialSearchSpec],
      classOf[SearchOrderSpec]).flatMap { s => createSpecification(s.getName)(Arguments()) }
    specs.
      foldLeft(t.title) { (res, cur) => res ^ link(cur) }
  }
}

@RunWith(classOf[JUnitRunner])
class WP6 extends Specification with SpecificationsFinder {
  def is =

    examplesLinks("WP 6: Check CSW service")

  def examplesLinks(t: String) = {
    val specs = List(
      classOf[CswGetCapabilitiesServiceUrlSpec],
      classOf[CswLanguageSpec],
      classOf[CswOutputSchemaSpec],
      classOf[CswTransactionalXmlTestSpec],
      classOf[CswXmlTestSpec],
      classOf[CswDublinCoreUriSpec]
      ).flatMap { s => createSpecification(s.getName)(Arguments()) }
    specs.
      foldLeft(t.title) { (res, cur) => res ^ link(cur) }
  }
}
@RunWith(classOf[JUnitRunner])
class WP7 extends Specification with SpecificationsFinder {
  def is =

    examplesLinks("WP 7: Search user interface")

  def examplesLinks(t: String) = {
    val specs = List(
      classOf[MefExportSpec]
      ).flatMap { s => createSpecification(s.getName)(Arguments()) }
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
      classOf[UpdateNonXlinkViaMetadataUpdate]).flatMap { s => createSpecification(s.getName)(Arguments()) }
    specs.
      foldLeft(t.title) { (res, cur) => res ^ link(cur) }
  }
}
@RunWith(classOf[JUnitRunner])
class WP16 extends Specification with SpecificationsFinder {
  def is =

    examplesLinks("WP 16: Misc. tests")

  def examplesLinks(t: String) = {
    val specs = List(
      classOf[MetadataValidationReportSpec],
      classOf[PreStyleSheetSpec],
      classOf[XmlInfoServiceLocalisationSpec]).flatMap { s => createSpecification(s.getName)(Arguments()) }
    specs.foldLeft(t.title) { (res, cur) => res ^ link(cur) }
  }
}
