package c2c.webspecs
import java.io.IOException


object ChainedRequest {
  class ConstantRequestFunction[A,B](request:Request[A,B]) extends Function[Response[A],Request[A,B]] {
    override def apply(v1: Response[A]) = request
    override def toString() = request.toString
  }
  def apply[A,B,C](first:Request[A,B],second:Request[B,C]) = {
    new ChainedRequest(first,new ConstantRequestFunction(second))
  }
  def apply[A,B,C](first:Request[A,B],second:Response[B] => Request[B,C]) = {
    new ChainedRequest(first,second)
  }
}

class ChainedRequest[-A,B,+C] private(first:Request[A,B],second:Function[Response[B], Request[B,C]]) extends Request[A,C] {
  def execute (in: A)(implicit context:ExecutionContext, uriResolvers:UriResolver) = {
    first.execute(in) match {
      case response if response.basicValue.responseCode > 399 =>
        val basicValue = response.basicValue
        throw new IOException("Executing "+first+" failed with a "+basicValue.responseCode+" responseCode, message = "+basicValue.responseMessage)//+"\ntext:\n"+response.text)
      case response =>
        second(response).execute(response.value)
    }
  }
}
