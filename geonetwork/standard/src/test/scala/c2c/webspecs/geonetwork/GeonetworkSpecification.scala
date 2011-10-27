package c2c.webspecs
package geonetwork


import org.specs2._
import specification._
import UserProfiles._
import c2c.webspecs.ExecutionContext
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.MatchResult
import org.specs2.execute.Result
import scala.xml.NodeSeq
import java.util.UUID
import scala.collection.mutable.SynchronizedQueue

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

  lazy val uuid = UUID.randomUUID
  
  private var mdToDelete = new SynchronizedQueue[Id]()
  
  def registerNewMd(ids:Id*):Unit = mdToDelete ++= ids
  
  override def extraSetup(setupContext:ExecutionContext) = {
    super.extraSetup(setupContext)
    UserLogin.execute(None)(context)
  }
  
  override def extraTeardown(tearDownContext:ExecutionContext) = {
    super.extraTeardown(tearDownContext)
    
    config.adminLogin()
    
    mdToDelete foreach {id => 
      try {DeleteMetadata.execute(id) }
      catch { case _ => println("Error deleting: "+ id) }
    }
  }
  
  
  
}
