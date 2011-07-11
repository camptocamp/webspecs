package c2c.webspecs
package geonetwork

import c2c.webspecs.login.LoginRequest

class SandboxLifeCycle(config:GeonetConfig) extends SystemLifeCycle {
  import config._

  def setup(implicit context: ExecutionContext) = {

    val createGroup = CreateGroup(new Group(user))
    val groupId = (adminLogin then createGroup).assertPassed(None).value.id
    val createUser = CreateUser(User(username=user, password=pass, profile=userProfile, groups=List(groupId)))

    val request = (createUser then LoginRequest(user, pass))
    request.assertPassed(None)
  }


  def tearDown(implicit context: ExecutionContext) = {

    val DeleteMetadata = GetUser.fromUserName(user) then DeleteOwnedMetadata

    val response = (adminLogin then DeleteMetadata then DeleteGroup(groupId,true))(None)
    if(response.basicValue.responseCode > 200)
      throw new AssertionError("Error occurred during teardown.  Group was not deleted.  ResponseCode = "+response.basicValue.responseCode)
  }
}

class CreateAsNeededUserLifeCycle(config:GeonetConfig)  extends SystemLifeCycle {
  import config._
  def setup(implicit context: ExecutionContext) = {
    val loginAsUserCode = LoginRequest(user, pass)(None).basicValue.responseCode
    if(loginAsUserCode > 200) {
      adminLogin.assertPassed(None)
      val groupName = Properties("group") getOrElse {throw new IllegalArgumentException("A group configuration parameter is required")}
      val gid = if(! ListGroups(None).value.exists(_.name == groupName)) {
        println("Creating group")
        val group = Group(name = groupName, description = "Test Groups")
        CreateGroup(group).assertPassed(None).value.id
      } else {
        groupId
      }
      println("Creating user")
      val testUser = User(idOption = Some(user), username = user, password = pass, profile = userProfile, groups = Seq(gid))
      CreateUser(testUser).assertPassed(None)
      println("Done creating user")
    }
    LoginRequest(user, pass).assertPassed(None)
  }

  def tearDown(implicit context: ExecutionContext) = {

    val DeleteMetadata = GetUser.fromUserName(user) then DeleteOwnedMetadata

    (adminLogin then DeleteMetadata)(None)
  }
}


class PredefinedUserLifeCycle(config:GeonetConfig)  extends SystemLifeCycle {
  import config._
  def setup(implicit context: ExecutionContext) = {
    LoginRequest(user, pass).assertPassed(None)
  }

  def tearDown(implicit context: ExecutionContext) = {

    val DeleteMetadata = GetUser.fromUserName(user) then DeleteOwnedMetadata

    val response = (adminLogin then DeleteMetadata)(None)
    if(response.basicValue.responseCode > 200)
      throw new AssertionError("Error occurred during teardown.  Group was not deleted.  ResponseCode = "+response.basicValue.responseCode)

  }
}
