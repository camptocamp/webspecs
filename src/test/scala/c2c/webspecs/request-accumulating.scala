package c2c.webspecs

import java.io.IOException

object AccumulatingRequest {
  import ChainedRequest.ConstantRequestFunction

  def apply[A,B,C](first:Request[A,B],second:Request[B,C],track:Boolean):AccumulatingRequest[A,C] = {
    new AccumulatingRequestImpl(first,new ConstantRequestFunction(second),track)
  }
  def apply[A,B,C](first:Request[A,B],second:Response[B] => Request[B,C],track:Boolean):AccumulatingRequest[A,C] = {
    new AccumulatingRequestImpl(first,second,track)
  }
}

trait AccumulatingRequest[-In,+Out] extends Request[In,Out] {
  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse[Out] = super.assertPassed(in).asInstanceOf[AccumulatedResponse[Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest[In, A] = AccumulatingRequest(this,next,false)
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest[In, A] = AccumulatingRequest(this,next,false)
  override def trackThen [A,B] (next: Request[Out,A]) : AccumulatingRequest[In, A] = AccumulatingRequest(this,next,true)
  override def trackThen [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest[In, A] = AccumulatingRequest(this,next,true)

  override def compose [A,B] (before: Request[A,In]) : AccumulatingRequest[A, Out] = AccumulatingRequest(before,this,false)

  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse[Out]
}

private class AccumulatingRequestImpl[-A,B,+C](first:Request[A,B], second:Function[Response[B], Request[B,C]]) extends AccumulatingRequest[A,C] {
  def apply(in: A)(implicit context: ExecutionContext):AccumulatedResponse[C] = {
    first.apply(in) match {
      case response if response.basicValue.responseCode > 399 =>
        val basicValue = response.basicValue
        throw new IOException("Executing "+first+" failed with a "+basicValue.responseCode+" responseCode, message = "+basicValue.responseMessage)//+"\ntext:\n"+response.text)
      case response =>
        second(response).apply(response.value)
    }
  }

  override def toString() = current+" trackThen "+next
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
  def apply(previous:Response, mostRecent:Response):AccumulatedResponse = {
    (previous,mostRecent) match {
      case (previous:AccumulatedResponse, mostRecent) =>
        val filtered = previous.responses ::: previous.last :: Nil filterNot{_ == EmptyResponse}
        new AccumulatedResponse(filtered, mostRecent)
      case (previous, mostRecent) =>
        new AccumulatedResponse(List(previous),mostRecent)
    }
  }
  def unapplySeq(response:AccumulatedResponse):Option[Seq[Response]] = Some(response.responses ::: response.last :: Nil)
  object Last {
    def unapply(response:AccumulatedResponse):Option[Response] = Some(response.last)
  }
}

class AccumulatedResponse[+A](val tracked:List[Response[_]], val last:Response[A]) extends Response[A] {
  def basicValue = last.basicValue

  def value = last.value

  def apply (responseIndex:Int) = responses(responseIndex)
  def dontTrack(next:Response) = {
    new AccumulatedResponse(responses,next)
  }

  override def toString = "AccumulatedResponse("+responses.mkString(",")+")"

}