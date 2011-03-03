package c2c.webspecs

import java.io.IOException

object AccumulatingRequest {
  import ChainedRequest.ConstantRequestFunction

  def apply[A,B,C](first:Request[A,B], trackFirst:Boolean, second:Request[B,C]):AccumulatingRequest[A,C] = {
    new AccumulatingRequestImpl(first,trackFirst,new ConstantRequestFunction(second))
  }
  def apply[A,B,C](first:Request[A,B], trackFirst:Boolean, second:Response[B] => Request[B,C]):AccumulatingRequest[A,C] = {
    new AccumulatingRequestImpl(first,trackFirst,second)
  }
}

trait AccumulatingRequest[-In,+Out] extends Request[In,Out] {
  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse[Out] = super.assertPassed(in).asInstanceOf[AccumulatedResponse[Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest[In, A] = AccumulatingRequest(this,false,next)
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest[In, A] = AccumulatingRequest(this,false,next)
  override def trackThen [A,B] (next: Request[Out,A]) : AccumulatingRequest[In, A] = AccumulatingRequest(this,true,next)
  override def trackThen [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest[In, A] = AccumulatingRequest(this,true,next)

  override def map[A <: In](in: A) = {
    AccumulatingRequest(Request.const(in),false,this)
  }

  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse[Out]
}

private class AccumulatingRequestImpl[-A,B,+C](first:Request[A,B], trackFirst:Boolean,
                                               second:Response[B] => Request[B,C]) extends AccumulatingRequest[A,C] {
  def apply(in: A)(implicit context: ExecutionContext):AccumulatedResponse[C] = {
    val result = AccumulatedResponse(first(in))

    result match {
      case response if response.basicValue.responseCode > 399 =>
        val basicValue = response.basicValue
        throw new IOException("Executing "+first+" failed with a "+basicValue.responseCode+" responseCode, message = "+basicValue.responseMessage)//+"\ntext:\n"+response.text)
      case response if trackFirst =>
        result.track(second(response).apply(response.value))
      case response =>
        result.dontTrack(second(response).apply(response.value))
    }
  }

  override def toString() = first+" trackThen "+second
}
/*
private[geonet] class AccumulatingResponseRequest[R1<:Request](previous:Response, request:R1) extends AccumulatingRequest[R1] {
  def exec(sideEffect: Option[SideEffect]) = {
    val combined = sideEffect match {
      case Some(e) => e.addEffect(previous.effect)
      case None => previous.effect
    }
    val latestResponse = request.exec(Some(combined))
    AccumulatedResponse(previous,latestResponse)
  }
}
*/

object AccumulatedResponse {
  def apply[A](response:Response[A]):AccumulatedResponse[A] = response match {
    case response:AccumulatedResponse[A] => response
    case _ => new AccumulatedResponse(Nil, response)
  }
  def apply[A](tracked:List[Response[_]],next:Response[A]):AccumulatedResponse[A] = next match {
    case next:AccumulatedResponse[A] => new AccumulatedResponse(tracked ::: next.tracked, next.last)
    case _ => new AccumulatedResponse(tracked, next)
  }

  object Tracked {
    def unapplySeq[A](response:AccumulatedResponse[A]):Option[Seq[Response[_]]] = Some(response.tracked)
  }
  object IncludeLast {
    def unapplySeq[A](response:AccumulatedResponse[A]):Option[Seq[Response[_]]] = Some(response.tracked ::: response.last :: Nil)
  }
  object Last {
    def unapply[A](response:AccumulatedResponse[A]):Option[Response[A]] = Some(response.last)
  }
}

class AccumulatedResponse[+A](val tracked:List[Response[_]], val last:Response[A]) extends Response[A] {
  def basicValue = last.basicValue

  def value = last.value

  def apply (responseIndex:Int) = tracked(responseIndex)

  private def nextTracked(trackLast:Boolean) = if (trackLast) (tracked ::: last :: Nil) filter {_ != EmptyResponse} else tracked
  def dontTrack[B](next:Response[B]) = AccumulatedResponse (nextTracked(false),next)
  def track[B](next:Response[B]) = AccumulatedResponse (nextTracked(true),next)

  override def toString = "AccumulatedResponse("+tracked.mkString(",")+")"

}