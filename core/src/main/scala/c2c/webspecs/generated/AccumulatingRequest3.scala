package c2c.webspecs
package generated

import AccumulatingRequest._
import ChainedRequest.ConstantRequestFunction


class AccumulatingRequest3[-In,+T1,+T2,+T3,+Out](
    last:Response[T3] => Request[T3,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse3[T1,T2,T3,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse3[T1,T2,T3,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest3[In, T1,T2,T3,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest3[In, T1,T2,T3, A] =
    new AccumulatingRequest3(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest4[In,T1,T2,T3,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest4[In,T1,T2,T3,Out,A] =
  new AccumulatingRequest4[In,T1,T2,T3,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def setIn[A <: In](in: A) =
    new AccumulatingRequest3[Any, T1,T2,T3,Out](last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse3[T1,T2,T3,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse3(
      trackedResponses(0).asInstanceOf[Response[T1]],
      trackedResponses(1).asInstanceOf[Response[T2]],
      trackedResponses(2).asInstanceOf[Response[T3]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}
