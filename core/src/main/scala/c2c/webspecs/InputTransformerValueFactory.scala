package c2c.webspecs

case class InputTransformerValueFactory[A,B](f:(A,BasicHttpValue) => B) extends ValueFactory[A,B] {
    def createValue[C <: A, D >: B]
                 (request: Request[C, D],
                  in: A,
                  rawValue:BasicHttpValue,
                  executionContext: ExecutionContext, 
                  uriResolver:UriResolver) = f(in,rawValue)
}