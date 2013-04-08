package c2c.webspecs
package geonetwork
package geocat

import shared._
import java.util.UUID
import c2c.webspecs.geonetwork.GeonetConfig
import c2c.webspecs.ExecutionContext
import scala.xml.Node
import c2c.webspecs.geonetwork.geocat.shared.CreateValidatedUser
import c2c.webspecs.geonetwork.geocat.shared.CreateNonValidatedUser
import c2c.webspecs.geonetwork.geocat.shared.DeleteSharedUser

/**
 * Fixtures that only apply in Geocat
 */
object GeocatFixture {
    def sharedUser(validated:Boolean) = new Fixture[GeonetConfig] {
    val name = "Web"
    val lastname = "Specs"
    val username = "WebSpecs_"+UUID.randomUUID().toString
    val email = "Webspecs@camptocamp.com"
    private var _id:String = _
    val position = LocalisedString(en = "EN developer", de = "DE Entwickler", fr = "FR développeur")

    def id = _id
    
    def user = User(
        idOption = Option(id),
        username = username,
        email = email,
        name = name,
        surname=lastname,
        password = username,
        position = position,
        contactInstructions = Some(LocalisedString(en = "contactInstructionEN", fr="contactInstructionFR")),
        profile = SharedUserProfile) 

    def delete(config: GeonetConfig, context: ExecutionContext, uriResolver:UriResolver) =
      (config.adminLogin then DeleteSharedUser(id,true)).execute()(context,uriResolver)

    def create(config: GeonetConfig, context: ExecutionContext, uriResolver:UriResolver) = {
      val userReq = user
      val createRequest = 
        if(validated) CreateValidatedUser(userReq)
        else          CreateNonValidatedUser(userReq)
        
      val createResponse = (config.adminLogin then createRequest).execute()(context, uriResolver)
      _id = createResponse.value.userId
    }
  }
  def format = new Fixture[GeonetConfig] {
    val name = "WebSpecs"
    val version = UUID.randomUUID().toString
    private var _id:Int = -1

    def id = _id

    def delete(config: GeonetConfig, context: ExecutionContext, uriResolver:UriResolver) =
      (config.adminLogin then DeleteFormat(true).setIn(id)).execute()(context, uriResolver)

    def create(config: GeonetConfig, context: ExecutionContext, uriResolver:UriResolver) = {
      val formats = (config.adminLogin then AddFormat(name, version) then ListFormats.setIn(name)).execute()(context,uriResolver)
      _id = formats.value.find(_.version == version).get.id
    }
  }
  
  def reusableExtent(extentXml:Node) = new Fixture[GeonetConfig] {
    private var _id:String = _
    def id = _id
    
    def delete(config: GeonetConfig, context: ExecutionContext, uriResolver:UriResolver) =
      (config.adminLogin then DeleteExtent(Extents.NonValidated,id,true)).execute()(context,uriResolver)

    def create(config: GeonetConfig, context: ExecutionContext, uriResolver:UriResolver) = {
      val extents = (config.adminLogin then ProcessSharedObject(extentXml, true)).execute()(context, uriResolver)
      val xml = extents.value
      _id = XLink.id(extents.value \\ "extent" head).get
    }
  }
}