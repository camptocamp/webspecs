package c2c.webspecs
package geonetwork

import login._

class SandboxLifeCycle(config:GeonetConfig) extends SystemLifeCycle {
  import config._

  def setup(implicit context: ExecutionContext, uriResolver:UriResolver) = {

    val createGroup = CreateGroup(new Group(user))
    val groupId = (adminLogin then createGroup).assertPassed(None).value.id
    val createUser = CreateUser(User(username=user, password=pass, profile=userProfile, groups=List(groupId)))

    val request = (createUser then LoginRequest(user, pass))
    request.assertPassed(None)
  }


  def tearDown(implicit context: ExecutionContext, uriResolver:UriResolver) = {

    val DeleteMetadata = GetUser.fromUserName(user) then DeleteOwnedMetadata

    val response = (adminLogin then DeleteMetadata then DeleteGroup(groupId,true)).execute()
    if(response.basicValue.responseCode > 200)
      throw new AssertionError("Error occurred during teardown.  Group was not deleted.  ResponseCode = "+response.basicValue.responseCode)
  }
}