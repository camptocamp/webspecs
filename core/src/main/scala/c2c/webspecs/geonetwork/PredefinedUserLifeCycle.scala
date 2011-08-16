package c2c.webspecs
package geonetwork

import c2c.webspecs.login.LoginRequest

class PredefinedUserLifeCycle(config:GeonetConfig)  extends SystemLifeCycle {
  import config._
  def setup(implicit context: ExecutionContext) = {
    LoginRequest(user, pass).assertPassed(None)
  }

  def tearDown(implicit context: ExecutionContext) = {}
}
