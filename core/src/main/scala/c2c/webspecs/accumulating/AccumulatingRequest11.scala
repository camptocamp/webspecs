package c2c.webspecs
package accumulating

import AccumulatingRequest._
import ChainedRequest.ConstantRequestFunction


class AccumulatingRequest11[-In,+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+Out](
    last:Response[T11] => Request[T11,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse11[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse11[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest11[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest11[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11, A] =
    new AccumulatingRequest11(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest12[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest12[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,Out,A] =
  new AccumulatingRequest12[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def setIn[A <: In](in: A) =
    new AccumulatingRequest11[Any, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,Out](last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse11[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse11(
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
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}
