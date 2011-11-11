package c2c.webspecs
package geonetwork

import c2c.webspecs.ExecutionContext
import java.util.UUID

object GeonetworkFixture {
  def user(requiredProfile:UserProfiles.UserProfile = UserProfiles.Editor) = new Fixture[GeonetConfig] {
    val profile = requiredProfile
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
        profile = profile) 

    def delete(config: GeonetConfig, context: ExecutionContext, uriResolvers:UriResolver) =
      (config.adminLogin then DeleteUser(id)).execute()(context,uriResolvers)

    def create(config: GeonetConfig, context: ExecutionContext, uriResolvers:UriResolver) = {
      val userReq = user
      val createResponse = (config.adminLogin then CreateUser(userReq)).execute()(context,uriResolvers)
      _id = createResponse.value.userId
    }
  }
  
  def keyword(_namespace:String, _thesaurus:String) = new Fixture[GeonetConfig] {
    val namespace = _namespace
    val thesaurus = _thesaurus
    lazy val uuid = UUID.randomUUID().toString() 
    lazy val en = "EN-"+uuid
    lazy val fr = "FR-"+uuid
    lazy val de = "DE-"+uuid
    lazy val it = "IT-"+uuid
    
    lazy val id = uuid
    
    def delete(config: GeonetConfig, context: ExecutionContext, uriResolvers:UriResolver) =
      (config.adminLogin then DeleteKeyword(thesaurus,namespace,id,true)).execute()(context,uriResolvers)

    def create(config: GeonetConfig, context: ExecutionContext, uriResolvers:UriResolver) = {
      val request = (config.adminLogin then CreateKeyword(namespace,id,thesaurus,"EN" -> en, "FR" -> fr, "DE" -> de, "IT" -> it))
      request.execute()(context,uriResolvers)
    }
  }
}