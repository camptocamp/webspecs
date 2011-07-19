package c2c.webspecs
package geonetwork

import c2c.webspecs.login.LoginRequest

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
