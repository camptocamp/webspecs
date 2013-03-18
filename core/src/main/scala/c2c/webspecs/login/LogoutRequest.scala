package c2c.webspecs
package login


object LogoutRequest {
  private lazy val request = Config.loadStrategy[Request[Any,Any]]("logout") fold (
    throw _,
    strategy =>
      strategy.newInstance()
  )
  def apply():Request[Any,Any] = request
}

trait LogoutRequest extends Request[Any,Any] 
