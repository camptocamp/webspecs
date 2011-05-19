package c2c.webspecs
package generated

import AccumulatingRequest._
import ChainedRequest.ConstantRequestFunction


class AccumulatingRequest15[-In,+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+T15,+Out](
    last:Response[T15] => Request[T15,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse15[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse15[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest15[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest15[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15, A] =
    new AccumulatingRequest15(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest16[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest16[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,Out,A] =
  new AccumulatingRequest16[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def setIn[A <: In](in: A) =
    new AccumulatingRequest15[Any, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,Out](last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse15[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse15(
      trackedResponses(0).asInstanceOf[Response[T1]],
      trackedResponses(1).asInstanceOf[Response[T2]],
      trackedResponses(2).asInstanceOf[Response[T3]],
      trackedResponses(3).asInstanceOf[Response[T4]],
      trackedResponses(4).asInstanceOf[Response[T5]],
      trackedResponses(5).asInstanceOf[Response[T6]],
      trackedResponses(6).asInstanceOf[Response[T7]],
      trackedResponses(7).asInstanceOf[Response[T8]],
      trackedResponses(8).asInstanceOf[Response[T9]],
      trackedResponses(9).asInstanceOf[Response[T10]],
      trackedResponses(10).asInstanceOf[Response[T11]],
      trackedResponses(11).asInstanceOf[Response[T12]],
      trackedResponses(12).asInstanceOf[Response[T13]],
      trackedResponses(13).asInstanceOf[Response[T14]],
      trackedResponses(14).asInstanceOf[Response[T15]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}
