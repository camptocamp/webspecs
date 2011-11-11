package c2c.webspecs

trait ValueFactory[-In,+Out] {
  def createValue[A <: In, B >: Out](request:Request[A,B],in:In,rawValue:BasicHttpValue,executionContext:ExecutionContext, uriResolver:UriResolver):Out
}
