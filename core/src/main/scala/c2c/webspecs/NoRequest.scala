package c2c.webspecs

object NoRequest extends Request[Any,Null] {
  def execute(in: Any)(implicit context: ExecutionContext, uriResolvers:UriResolver) = EmptyResponse
  override def toString() = "NoRequest"
}
