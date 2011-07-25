package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step
import c2c.webspecs.{XmlValue, Response}
import geonetwork.UserRef
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner]) 
class AddXLinks extends GeocatSpecification { def is = 
  "AddLinks to metadata".title 																		^ 
  sequential 																						^
  "This specification adds XLinks to existing metadata "      										^ Step(setup) ^ Step(ImportMdId) ^ 
  	("Adding an ${contact} XLink to metadata should result in the " +
  			"next access of the metadata containing the new contact")  								! addXLink ^ 
  	"Updating shared ${contact} should result in the metadata being updated in metadata as well" 	! updateXLink ^ end ^ 
                                                           											  Step(tearDown)

  lazy val ImportMdId = {
    val (_, importMd) = ImportMetadata.defaults(uuid, "/geocat/data/bare.iso19139.che.xml", false, getClass)
    importMd(None).value.id
  }
  val addXLink = (s: String) => success
  val updateXLink = (s: String) => success

  override def extraTeardown(teardownContext:ExecutionContext):Unit = {
    DeleteMetadata(Id(ImportMdId)) (teardownContext)
    super[GeocatSpecification].extraTeardown(teardownContext)
  }
  
  lazy val userFixture = GeonetworkFixture.user(SharedUserProfile)
  lazy val keywordFixture = GeonetworkFixture.keyword(GeocatConstants.KEYWORD_NAMESPACE, GeocatConstants.NON_VALIDATED_THESAURUS)
  lazy val formatFixture = GeocatFixture.format
  override lazy val fixtures = Seq(userFixture, keywordFixture, formatFixture)
}