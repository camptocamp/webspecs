package c2c.webspecs
package accumulating

import AccumulatingRequest._
import ChainedRequest.ConstantRequestFunction


class AccumulatingRequest1[-In,+T1,+Out](
    last:Response[T1] => Request[T1,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse1[T1,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse1[T1,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest1[In, T1,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest1[In, T1, A] =
    new AccumulatingRequest1(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest2[In,T1,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest2[In,T1,Out,A] =
  new AccumulatingRequest2[In,T1,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def setIn[A <: In](in: A) =
    new AccumulatingRequest1[Any, T1,Out](last, Elem(Request.const(in),false) +: elems: _*)


  def execute(in: In)(implicit context: ExecutionContext):AccumulatedResponse1[T1,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse1(
      trackedResponses(0).asInstanceOf[Response[T1]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}
