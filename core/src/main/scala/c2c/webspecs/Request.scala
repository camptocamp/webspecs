package c2c.webspecs

object Request {
  def const[A](in:A) = new Request[Any,A] {
    def execute(empty: Any)(implicit context: ExecutionContext, uriResolvers:UriResolver) = Response(in)
  }
}
trait Request[-In, +Out] {
  /**
   * Create a new Request that will first execute this request then the following test
   */
  def then [A,B] (next: Request[Out,A]) : Request[In, A] = ChainedRequest(this,next)

  /**
   * Creates a new Chained request where first this Request is executed, then the 'next'
   * function is used to create a second request to be executed.
   *
   * This is normally only needed to create complex requests and typically can be ignored in
   * Specification implementation
   */
  def then [A,B] (next: Response[Out] => Request[Out,A]) : Request[In, A] = ChainedRequest(this,next)
  /** Set the input value resulting in a new Request that doesn't need an input value for execution */
  def setIn[A <: In](in:A):Request[Any,Out] = Request.const(in) then this
  /** Create a new Request whos output object will have the function applied to it's value (after the request is executed*/
  def map[A] (mapping: Out => A):Request[In,A] = {
    val outer = this;
    new Request[In,A] {
      def execute(in: In)(implicit context: ExecutionContext, uriResolvers:UriResolver) = outer.execute(in).map(mapping)
    }
  }

  /**
   * Execute the request.  Typically the context and uri resolvers do not need to be explicitely declared because
   * the WebSpecification class declares these and they will be automatically added.
   * Example usage:
   * {{{request.execute(inputValue)}}}
   *
   * If the request's input type is Any then the input value can be ignored and the request can
   * be executed as follows: {{{request.execute()}}}
   */
  def execute (in: In)(implicit context:ExecutionContext=new DefaultExecutionContext(), uriResolvers:UriResolver) : Response[Out]

  /**
   * Execute the Request and perform a that the response
   */
  def assertPassed(in:In)(implicit context:ExecutionContext=new DefaultExecutionContext(), uriResolvers:UriResolver) = execute(in) match {
    case response if response.basicValue.responseCode > 399 =>
      throw new AssertionError(toString+" did not complete correctly, reponseCode="+
        response.basicValue.responseCode+" message: "+
        response.basicValue.responseMessage)
    case response => response
  }
}
