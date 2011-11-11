package c2c.webspecs

object SelfValueFactory {
  def apply[In,Out]() = new ValueFactory[In,Out]{
    def createValue[A <: In, B >: Out](request:Request[A,B],in:In,rawValue:BasicHttpValue,executionContext:ExecutionContext, uriResolver:UriResolver):Out = {
      request.asInstanceOf[ValueFactory[In,Out]].createValue(request,in,rawValue,executionContext,uriResolver)
    }
  }
}
