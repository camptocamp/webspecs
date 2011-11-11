package c2c.webspecs

case class PassThroughValueFactory[A]() extends ValueFactory[A,A] {
  def createValue[B <: A, C >: A]
                 (request: Request[B, C],
                  in: A,
                  rawValue:
                  BasicHttpValue,
                  executionContext: ExecutionContext, 
                  uriResolver:UriResolver) = in
}

