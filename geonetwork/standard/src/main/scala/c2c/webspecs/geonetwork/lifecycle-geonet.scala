package c2c.webspecs
package geonetwork

class SandboxLifeCycle(config:GeonetConfig) extends SystemLifeCycle {
    import config._

  def setup(implicit context: ExecutionContext) = {

    val createGroup = CreateGroup(user)
    val createUser = CreateUser(User(username=user, password=pass, profile=userProfile, groups=List(groupId)))

    (adminLogin then createGroup then createUser then Login(user, pass)).assertPassed(None)
  }


  def tearDown(implicit context: ExecutionContext) = {

    val DeleteMetadata = GetUser.fromUserName(user) then DeleteOwnedMetadata

    val response = (adminLogin then DeleteMetadata then DeleteGroup(groupId,true))(None)
    if(response.basicValue.responseCode > 200)
      throw new AssertionError("Error occurred during teardown.  Group was not deleted.  ResponseCode = "+response.basicValue.responseCode)
  }
}


class PredefinedUserLifeCycle(config:GeonetConfig)  extends SystemLifeCycle {
  import config._
  def setup(implicit context: ExecutionContext) = {
    Login(user, pass).assertPassed(None)
  }

  def tearDown(implicit context: ExecutionContext) = {

    val DeleteMetadata = GetUser.fromUserName(user) then DeleteOwnedMetadata

    val response = (adminLogin then DeleteMetadata)(None)
    if(response.basicValue.responseCode > 200)
      throw new AssertionError("Error occurred during teardown.  Group was not deleted.  ResponseCode = "+response.basicValue.responseCode)

  }
}
