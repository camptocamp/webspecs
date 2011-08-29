package c2c.webspecs
package geonetwork
package geocat

import java.util.UUID
import c2c.webspecs.geonetwork.GeonetConfig
import c2c.webspecs.ExecutionContext
import scala.xml.Node

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
        profile = SharedUserProfile) 

    def delete(config: GeonetConfig, context: ExecutionContext) =
      (config.adminLogin then DeleteSharedUser(id,true))(None)(context)

    def create(config: GeonetConfig, context: ExecutionContext) = {
      val userReq = user
      val createRequest = 
        if(validated) CreateValidatedUser(userReq)
        else          CreateNonValidatedUser(userReq)
        
      val createResponse = (config.adminLogin then createRequest)(None)(context)
      _id = createResponse.value.userId
    }
  }
  def format = new Fixture[GeonetConfig] {
    val name = "WebSpecs"
    val version = UUID.randomUUID().toString
    private var _id:Int = -1

    def id = _id

    def delete(config: GeonetConfig, context: ExecutionContext) =
      (config.adminLogin then DeleteFormat(true).setIn(id))(None)(context)

    def create(config: GeonetConfig, context: ExecutionContext) = {
      val formats = (config.adminLogin then AddFormat(name, version) then ListFormats.setIn(name))(None)(context)
      _id = formats.value.find(_.version == version).get.id
    }
  }
  
  def reusableExtent(extentXml:Node) = new Fixture[GeonetConfig] {
    private var _id:String = _
    def id = _id
    
    def delete(config: GeonetConfig, context: ExecutionContext) =
      (config.adminLogin then DeleteExtent(Extents.NonValidated,id,true))(None)(context)

    def create(config: GeonetConfig, context: ExecutionContext) = {
      val extents = (config.adminLogin then ProcessSharedObject(extentXml, true))(None)(context)
      val xml = extents.value
      _id = XLink.id(extents.value \\ "extent" head).get
    }
  }
}