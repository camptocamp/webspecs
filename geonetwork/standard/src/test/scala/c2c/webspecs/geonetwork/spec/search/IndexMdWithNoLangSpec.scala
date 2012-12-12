package c2c.webspecs.geonetwork.spec.search

import c2c.webspecs._
import geonetwork._

import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class IndexMdWithNoLangSpec extends SampleDataGeonetworkSpecification {
  def is =
    "This spec verifies that metadata with no language will have CharacterString elements correctly indexed" ^ Step(setup) ^ Step(doImport) ^
    "Assert that the title (which is CharacterString) can be found when searching ${eng}" ! findTitle ^
                                                            Step (tearDown)

  val uuidString = uuid.toString.toUpperCase()
  lazy val doImport = {
    val md = importMd(1, "/geonetwork/data/no_default_lang.xml", uuidString)
    md.head
  }

  def mdId = doImport
  
  val findTitle = (spec:String) => {
    val lang = extract1(spec)
    implicit val langresolver = new GeonetworkURIResolver() {
      override def locale = lang
    }

    val result = XmlSearch().search("_id" -> mdId).execute().value
    
    result.records.head.title must_== ("CharString Title "+uuidString)
  }
    
}