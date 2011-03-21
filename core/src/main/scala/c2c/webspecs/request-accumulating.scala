package c2c.webspecs

import _root_.c2c.webspecs.ChainedRequest.ConstantRequestFunction
import java.io.IOException

object AccumulatingRequest {
  import ChainedRequest.ConstantRequestFunction

  object Elem {
    def apply(request:Request[Nothing,Any],track:Boolean):Elem =
      Elem(new ConstantRequestFunction(request.asInstanceOf[Request[Any,Any]]),track)
  }
  case class Elem(request:Response[Nothing] => Request[Nothing,Any],track:Boolean)
}

import AccumulatingRequest._
trait AccumulatingRequest[-In,+Out] extends Request[In,Out] {
  case class ResultData(lastResponse:Response[Any],
                         trackedResponses:Seq[Response[Any]])


  type RequestFactory = Response[Any] => Request[Any,Any]
  protected def doApply(in:Any,
                        last:Response[Any] => Request[Any,Any],
                        elems:Seq[Elem] )(implicit context: ExecutionContext) = {

    def processRequest(resultData:ResultData,
                       requestFactory:RequestFactory,
                       track:Boolean) = {
      import resultData._
      val request = requestFactory(lastResponse)
      request(lastResponse.value) match {
        case response if response.basicValue.responseCode > 399 =>
          val basicValue = response.basicValue
          throw new IOException("Executing "+request+" failed with a "+basicValue.responseCode+" responseCode, message = "+basicValue.responseMessage)//+"\ntext:\n"+response.text)
        case response if track =>
          ResultData(response, trackedResponses :+ response)
        case response =>
          ResultData(response, trackedResponses)
      }
    }

    val wrappedInput:Response[Any] = Response(in)
    val foldInitData = ResultData(wrappedInput,Seq[Response[Any]]())

    val resultData =
      (foldInitData /: elems) {
        case (resultData, Elem(requestFactory:RequestFactory,track)) =>
          processRequest(resultData,requestFactory,track)
      }

    processRequest(resultData,last.asInstanceOf[RequestFactory],false);
  }

  override def startTrackingThen[A, B](next: (Response[Out]) => Request[Out, A]) =
    throw new UnsupportedOperationException("AccumulatingRequests do not support startTrackingThen")

}

trait AccumulatedResponse[+Z] extends Response[Z] {
  self : Product =>
  def last:Response[Z]

  def basicValue = last.basicValue
  def value = last.value

  override def toString = productIterator.mkString(getClass.getSimpleName+"(",",",")")
}
class AccumulatingRequest0[-In,+Out](last:Response[Any] => Request[Any,Out],
                                     elems:Elem*)
  extends AccumulatingRequest[In,Out] {

  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest0[In, A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest0[In, A] =
    new AccumulatingRequest0(
      next.asInstanceOf[Response[Any] => Request[Any,A]],
      elems :+ new Elem(last,false) :_*)

  def trackThen [A,B] (next: Request[Out,A]) : AccumulatingRequest1[In,Out,A] =
    trackThen(new ConstantRequestFunction(next))
  def trackThen [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest1[In,Out,A] =
    new AccumulatingRequest1(next, elems :+ new Elem(last,true) :_*)
  override def setIn[A <: In](in: A) =
    new AccumulatingRequest0[Any,Out](last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):Response[Out] = {
    val resultData = doApply(in,last.asInstanceOf[RequestFactory],elems)
    resultData.lastResponse.asInstanceOf[Response[Out]]
  }

  override def toString() = elems.mkString("(",",",")")+"then"+last
}
