package c2c.webspecs
package accumulating

import AccumulatingRequest._
import ChainedRequest.ConstantRequestFunction


class AccumulatingRequest6[-In,+T1,+T2,+T3,+T4,+T5,+T6,+Out](
    last:Response[T6] => Request[T6,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse6[T1,T2,T3,T4,T5,T6,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse6[T1,T2,T3,T4,T5,T6,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest6[In, T1,T2,T3,T4,T5,T6,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest6[In, T1,T2,T3,T4,T5,T6, A] =
    new AccumulatingRequest6(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest7[In,T1,T2,T3,T4,T5,T6,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest7[In,T1,T2,T3,T4,T5,T6,Out,A] =
  new AccumulatingRequest7[In,T1,T2,T3,T4,T5,T6,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def setIn[A <: In](in: A) =
    new AccumulatingRequest6[Any, T1,T2,T3,T4,T5,T6,Out](last, Elem(Request.const(in),false) +: elems: _*)


  def execute(in: In)(implicit context: ExecutionContext):AccumulatedResponse6[T1,T2,T3,T4,T5,T6,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse6(
      trackedResponses(0).asInstanceOf[Response[T1]],
      trackedResponses(1).asInstanceOf[Response[T2]],
      trackedResponses(2).asInstanceOf[Response[T3]],
      trackedResponses(3).asInstanceOf[Response[T4]],
      trackedResponses(4).asInstanceOf[Response[T5]],
      trackedResponses(5).asInstanceOf[Response[T6]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}
