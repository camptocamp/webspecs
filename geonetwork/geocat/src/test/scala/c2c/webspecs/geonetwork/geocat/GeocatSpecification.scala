package c2c.webspecs
package geonetwork
package geocat

import shared._
import scala.xml.NodeSeq
import UserProfiles._

trait GeocatSpecification extends GeonetworkSpecification {
	override def extraSetup(setupContext:ExecutionContext):Unit = {
		SharedUserProfile.allNames // ensure SharedUserProfile is loaded
		assert(UserProfiles.all contains SharedUserProfile, "SharedUserProfile has not been loaded");
	  	super.extraSetup(setupContext)
	  
	}
  
	override def extraTeardown(teardownContext:ExecutionContext):Unit = {
	  implicit val currentScopeContext = teardownContext
	  super.extraTeardown(teardownContext)

	  config.adminLogin.execute(None)
	  
	  val newUsers = GeocatListUsers.execute("").value.filter(user => user.username contains uuid.toString)
	  newUsers.foreach(user => DeleteSharedUser(user.userId,true).execute())
	  
	  val newFormats = ListFormats.execute("").value filter (_.name contains uuid.toString)
	  newFormats.foreach(format => DeleteFormat(true).execute(format.id))
	  
	  val thesauri = GeocatConstants.GEOCAT_THESAURUS :: GeocatConstants.NON_VALIDATED_THESAURUS :: Nil
	  val newKeywords = SearchKeywords(thesauri).execute(uuid.toString).value
	  newKeywords.foreach(word => DeleteKeyword(word, true).execute())
	}
	
	
  def hrefInElement(nodeName:String) = 
    (result:Response[NodeSeq]) => {
      val href = (result.value \\ nodeName \@ "xlink:href")
      (href must not beEmpty)
    }
  def hrefHost(nodeName:String) = (result:Response[NodeSeq],s:String) => {
    val href = (result.value \\ nodeName \@ "xlink:href")(0)
    href must startingWith(XLink.PROTOCOL)
  }

  override def importMd(numberOfRecords:Int, md:String = "/geocat/data/bare.iso19139.che.xml", identifier:String, styleSheet:ImportStyleSheets.ImportStyleSheet = ImportStyleSheets.NONE) =
    super.importMd(numberOfRecords,md,identifier,styleSheet)

}