package c2c.webspecs
package geonetwork
/*
class SandboxLifeCycle extends SystemLifeCycle[GeonetConfig] {

  def setup(config: GeonetConfig) = {
    import config._

    val createGroup = (_:Response[Any]) => CreateGroup(user)
    val createUser = (_:Response[Any]) => CreateUser(User(username=user, password=pass, profile=userProfile, groups=List(groupId)))

    (adminLogin then createGroup then createUser then Login(user, pass)).assertPassed
  }

  def tearDown(config: GeonetConfig) = {
    import config._

    val DeleteMetadata = findUsers(_ contains user) {
      case users if users.isEmpty => NoRequest
      case users =>
        val props = users map {id => PropertyIsLike("_owner",id)}
        val csw = XmlRequest("csw",mdSearchXml(props))

        val deleteRequest : Request =
          csw then {
            response =>
              val ids = response.xml.right.get \\ "info" \ "id"

              ((NoRequest:Request) /: ids){case (req, id) => req then GetRequest("metadata.delete", "id" -> id.text)}
          }
          deleteRequest
    }

    (adminLogin then DeleteMetadata then DeleteGroup(groupId,true)){
      case response if response.responseCode > 200 => System.err.println("Error occurred during teardown.  Group was not deleted.  ResponseCode = "+response.responseCode)
      case _ => ()
    }
  }
}

class PredefinedUserLifeCycle extends SystemLifeCycle[GeonetConfig] {
  def setup(config: GeonetConfig) = {
    import config._

    Login(user, pass).assertPassed
  }

  def tearDown(config: GeonetConfig) = {
    import config._

    val DeleteMetadata = findUsers(_ contains user) {
      case users if users.isEmpty => NoRequest
      case users =>
        val props = users map {id => PropertyIsLike("_owner",id)}
        val csw = XmlRequest("csw",mdSearchXml(props))

        val deleteRequest =
          csw then {
            response =>
              val ids = response.xml.right.get \\ "info" \ "id"

              ((NoRequest:Request) /: ids){case (req, id) => req then GetRequest("metadata.delete", "id" -> id.text)}
          }
          deleteRequest
    }

    (adminLogin then DeleteMetadata){
      case response if response.responseCode > 200 => System.err.println("Error occurred during teardown.  Group was not deleted.  ResponseCode = "+response.responseCode)
      case _ => ()
    }
  }
}
*/