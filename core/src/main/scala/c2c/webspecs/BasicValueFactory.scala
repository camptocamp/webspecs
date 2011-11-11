package c2c.webspecs

trait BasicValueFactory[Out] extends ValueFactory[Any,Out] {
  def createValue(rawValue:BasicHttpValue):Out
  def createValue[A <: Any, B >: Out](request:Request[A,B],
                            in:Any,
                            rawValue:BasicHttpValue,
                            executionContext:ExecutionContext, 
                            uriResolver:UriResolver):Out = createValue(rawValue)
}
