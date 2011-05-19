package c2c.webspecs
package accumulating

import AccumulatingRequest._
import ChainedRequest.ConstantRequestFunction


class AccumulatingRequest8[-In,+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+Out](
    last:Response[T8] => Request[T8,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse8[T1,T2,T3,T4,T5,T6,T7,T8,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse8[T1,T2,T3,T4,T5,T6,T7,T8,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest8[In, T1,T2,T3,T4,T5,T6,T7,T8,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest8[In, T1,T2,T3,T4,T5,T6,T7,T8, A] =
    new AccumulatingRequest8(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest9[In,T1,T2,T3,T4,T5,T6,T7,T8,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest9[In,T1,T2,T3,T4,T5,T6,T7,T8,Out,A] =
  new AccumulatingRequest9[In,T1,T2,T3,T4,T5,T6,T7,T8,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def setIn[A <: In](in: A) =
    new AccumulatingRequest8[Any, T1,T2,T3,T4,T5,T6,T7,T8,Out](last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse8[T1,T2,T3,T4,T5,T6,T7,T8,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse8(
      trackedResponses(0).asInstanceOf[Response[T1]],
      trackedResponses(1).asInstanceOf[Response[T2]],
      trackedResponses(2).asInstanceOf[Response[T3]],
      trackedResponses(3).asInstanceOf[Response[T4]],
      trackedResponses(4).asInstanceOf[Response[T5]],
      trackedResponses(5).asInstanceOf[Response[T6]],
      trackedResponses(6).asInstanceOf[Response[T7]],
      trackedResponses(7).asInstanceOf[Response[T8]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}
