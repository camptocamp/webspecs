package org.fao.geonet

import util.control.Exception

trait SystemLifeCycle {
  def tearDown(config:Config):Unit
  def setup(config:Config):Unit
}

class SandboxLifeCycle extends SystemLifeCycle {
  def setup(config: Config) = {
    import Config._
    import config.constants._

    val createGroup = (_:Response) => CreateGroup(user)
    val createUser = (_:Response) => CreateUser(User(username=user, password=pass, profile=userProfile, groups=List(groupId)))

    (adminLogin then createGroup then createUser then Login(user, pass)).assertPassed
  }

  def tearDown(config: Config) = {
    import Config._
    import config.constants._

    val DeleteMetadata = findUsers(_ contains user) {
      case users if users.isEmpty => NoRequest
      case users =>
        val props = users map {id => PropertyIsLike("_owner",id)}
        val csw = XmlRequest("csw",mdSearchXml(props))

        val deleteRequest : Request =
          csw then {
            response =>
              val ids = response.xml.right.get \\ "info" \ "id"

              ((NoRequest:Request) /: ids){case (req, id) => req then Get("metadata.delete", "id" -> id.text)}
          }
          deleteRequest
    }

    (adminLogin then DeleteMetadata then DeleteGroup(groupId,true)){
      case response if response.responseCode > 200 => System.err.println("Error occurred during teardown.  Group was not deleted.  ResponseCode = "+response.responseCode)
      case _ => ()
    }
  }
}


class PredefinedUserLifeCycle extends SystemLifeCycle {
  def setup(config: Config) = {
    import Config._
    import config.constants._

    (adminLogin then Login(user, pass)).assertPassed
  }

  def tearDown(config: Config) = {
    import Config._
    import config.constants._

    val DeleteMetadata = findUsers(_ contains user) {
      case users if users.isEmpty => NoRequest
      case users =>
        val props = users map {id => PropertyIsLike("_owner",id)}
        val csw = XmlRequest("csw",mdSearchXml(props))

        val deleteRequest : Request =
          csw then {
            response =>
              val ids = response.xml.right.get \\ "info" \ "id"

              ((NoRequest:Request) /: ids){case (req, id) => req then Get("metadata.delete", "id" -> id.text)}
          }
          deleteRequest
    }

    (adminLogin then DeleteMetadata){
      case response if response.responseCode > 200 => System.err.println("Error occurred during teardown.  Group was not deleted.  ResponseCode = "+response.responseCode)
      case _ => ()
    }
  }
}

object SystemLifeCycle {
  def apply() = Config.property("lifecycle") match {
    case Some(strategyName) =>
      val fullClassName = Exception.allCatch.opt(Class.forName(strategyName).asInstanceOf[Class[SystemLifeCycle]])
      val appendedPackage = Exception.allCatch.opt(Class.forName("org.fao.geonet."+ strategyName).asInstanceOf[Class[SystemLifeCycle]])
      val classDecl = fullClassName orElse appendedPackage getOrElse {throw new Exception("Tried "+strategyName+" and org.fao.geonet."+strategyName+" but was unable to create a SystemLifeCycle implementation")}
      classDecl.newInstance
    case _ =>
      new SandboxLifeCycle()
  }
}