package c2c.webspecs
package login


object LogoutRequest {
  def apply():Request[Any,Any] = Config.loadStrategy[Request[Any,Any]]("logout") fold (
    throw _,
    strategy =>
      strategy.newInstance()
  )
}

trait LogoutRequest extends Request[Any,Any] 
