package c2c.webspecs

object NoRequest extends Request[Any,Null] {
  def apply(in: Any)(implicit context: ExecutionContext) = EmptyResponse
  override def toString() = "NoRequest"
}
