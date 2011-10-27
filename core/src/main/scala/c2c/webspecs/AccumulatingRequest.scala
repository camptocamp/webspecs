package c2c.webspecs

import _root_.c2c.webspecs.ChainedRequest.ConstantRequestFunction
import accumulating.AccumulatingRequest1
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
      request.execute(lastResponse.value) match {
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
        case (rData, Elem(requestFactory:RequestFactory,track)) =>
          processRequest(rData,requestFactory,track)
      }

    processRequest(resultData,last.asInstanceOf[RequestFactory],false);
  }

  override def startTrackingThen[A, B](next: (Response[Out]) => Request[Out, A]) =
    throw new UnsupportedOperationException("AccumulatingRequests do not support startTrackingThen")

}

