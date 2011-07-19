package c2c.webspecs

trait AnyRequest[T] { self:Request[Any,T] =>
  def apply()(implicit context:ExecutionContext):Response[T] = apply(Nil)
}