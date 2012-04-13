package c2c.webspecs
package login


object LoginRequest {
  def apply(user:String,pass:String):Request[Any,Any] = Config.loadStrategy[Request[Any,Any]]("login") fold (
    throw _,
    strategy =>
      strategy.getConstructor(classOf[String],classOf[String]).newInstance(user,pass)
  )
}

trait LoginRequest extends Request[Any,Any] {
  def user:String
}
