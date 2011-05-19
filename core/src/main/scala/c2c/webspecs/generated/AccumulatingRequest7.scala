package c2c.webspecs
package generated

import AccumulatingRequest._
import ChainedRequest.ConstantRequestFunction


class AccumulatingRequest7[-In,+T1,+T2,+T3,+T4,+T5,+T6,+T7,+Out](
    last:Response[T7] => Request[T7,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse7[T1,T2,T3,T4,T5,T6,T7,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse7[T1,T2,T3,T4,T5,T6,T7,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest7[In, T1,T2,T3,T4,T5,T6,T7,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest7[In, T1,T2,T3,T4,T5,T6,T7, A] =
    new AccumulatingRequest7(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest8[In,T1,T2,T3,T4,T5,T6,T7,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest8[In,T1,T2,T3,T4,T5,T6,T7,Out,A] =
  new AccumulatingRequest8[In,T1,T2,T3,T4,T5,T6,T7,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def setIn[A <: In](in: A) =
    new AccumulatingRequest7[Any, T1,T2,T3,T4,T5,T6,T7,Out](last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse7[T1,T2,T3,T4,T5,T6,T7,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse7(
      trackedResponses(0).asInstanceOf[Response[T1]],
      trackedResponses(1).asInstanceOf[Response[T2]],
      trackedResponses(2).asInstanceOf[Response[T3]],
      trackedResponses(3).asInstanceOf[Response[T4]],
      trackedResponses(4).asInstanceOf[Response[T5]],
      trackedResponses(5).asInstanceOf[Response[T6]],
      trackedResponses(6).asInstanceOf[Response[T7]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}
