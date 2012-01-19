package c2c.webspecs
package geonetwork


import org.specs2._
import specification._
import UserProfiles._
import c2c.webspecs.ExecutionContext
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import java.util.UUID
import scala.collection.mutable.SynchronizedQueue
import csw._

@RunWith(classOf[JUnitRunner]) 
abstract class GeonetworkSpecification(userProfile: UserProfile = Editor) extends WebSpecsSpecification[GeonetConfig] {
  implicit val config = GeonetConfig(userProfile, getClass().getSimpleName)
  lazy val UserLogin = config.login

  object template extends Given[String] {
    def extract(text: String): String = extract1(text)
  }
  object randomTemplate extends Given[String] {
    def extract(text: String): String = config.sampleDataTemplateIds(0)
  }

  
  private var mdToDelete = new SynchronizedQueue[Id]()
  
  def registerNewMd(ids:Id*):Unit = mdToDelete ++= ids
  
  override def extraSetup(setupContext:ExecutionContext) = {
    super.extraSetup(setupContext)
    UserLogin.execute(None)(context, uriResolver)
  }
  
  override def extraTeardown(tearDownContext:ExecutionContext) = {
    super.extraTeardown(tearDownContext)
    
    config.adminLogin.execute()
    
    mdToDelete foreach {id => 
      try {DeleteMetadata.execute(id) }
      catch { case _ => println("Error deleting: "+ id) }
    }
  }

  def importMd(numberOfRecords:Int, md:String, identifier:String, styleSheet:ImportStyleSheets.ImportStyleSheet = ImportStyleSheets.NONE) = {
    val replacements = Map("{uuid}" -> identifier)
    val importRequest = ImportMetadata.defaultsWithReplacements(replacements,md,false,getClass,styleSheet)._2

    1 to numberOfRecords map {_ =>
      val id = importRequest.execute().value.id
      registerNewMd(Id(id))
      id
    }
  }

  def correctResults(numberOfRecords:Int, identifier:String) = (s:String) => {
    val xml = CswGetRecordsRequest(PropertyIsEqualTo("AnyText","Title"+identifier).xml).execute().value.getXml

    (xml \\ "@numberOfRecordsMatched").text.toInt must_== numberOfRecords
  }


}
