package c2c.webspecs
package geonetwork


import org.specs2._
import specification._
import UserProfiles._
import c2c.webspecs.ExecutionContext
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.collection.mutable.SynchronizedQueue
import csw._

@RunWith(classOf[JUnitRunner]) 
trait GeonetworkSpecification extends WebSpecsSpecification[GeonetConfig] with SearchSettingsSpecification {
  implicit val config = GeonetConfig(Editor, getClass().getSimpleName)
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
    implicit val implicitContext = setupContext
    getSearchSetting
    setSearchSetting(only="prefer_locale", sorted = false, ignored = false)
    // don't chain requests because SetSequential is available on all GN instances
    config.adminLogin.assertPassed()
    SetSequentialExecution(true).execute() 
    SetUseNRTManagerReopenThread(true).execute() 
    UserLogin.assertPassed()
  }
  
  override def extraTeardown(tearDownContext:ExecutionContext) = {
    super.extraTeardown(tearDownContext)
    
    implicit val implicitContext = tearDownContext
    config.adminLogin.execute()
    resetSearchSetting
    mdToDelete foreach {id => 
      try {DeleteMetadata.execute(id) }
      catch { case _: Throwable => println("Error deleting: "+ id) }
    }

    SetSequentialExecution(false).execute()
    SetUseNRTManagerReopenThread(false).execute()
  }

  def importMd(numberOfRecords:Int, md:String, identifier:String, styleSheet:ImportStyleSheets.ImportStyleSheet = ImportStyleSheets.NONE) = {
    val replacements = Map("{uuid}" -> identifier)
    val importRequest = ImportMetadata.defaultsWithReplacements(replacements,md,false,getClass,styleSheet)._2
    Thread.sleep(500)
    1 to numberOfRecords map {_ =>
      val id = importRequest.execute().value.id
      registerNewMd(Id(id))
      Id(id)
    }
  }

  

  def correctResults(numberOfRecords:Int, identifier:String) = (s:String) => {
    val results = XmlSearch().to(10).search('any -> ("Title"+identifier)).execute().value

    results.size must_== numberOfRecords
  }

  /**
   * delete all metadata.  If adminLogin is true it will login as admin,
   * delete all metadata and the login back in as the user.
   *
   * implicit parameter is present so that the method can be used by tearDown methods
   */
  def deleteAllMetadata(adminLogin:Boolean)(implicit executionContext:ExecutionContext) = {
    if(adminLogin) config.adminLogin.execute()

    var loops = 5
    def search() = XmlSearch().execute()
    while (search().value.records.nonEmpty && loops > 0) {
      (SelectAll then MetadataBatchDelete).execute()
      loops -= 1
    }

    assert(search().value.records.isEmpty, "Unable to delete all records")

    if(adminLogin) UserLogin.execute()
  }

}
