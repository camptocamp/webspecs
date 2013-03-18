package c2c.webspecs
package login


object LoginRequest {
  private lazy val requestClass = Config.loadStrategy[Request[Any,Any]]("login")
  def apply(user:String,pass:String):Request[Any,Any] = requestClass fold (
    throw _,
    strategy =>
      strategy.getConstructor(classOf[String],classOf[String]).newInstance(user,pass)
  )
}

trait LoginRequest extends Request[Any,Any] {
  def user:String
}
