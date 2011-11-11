package c2c.webspecs
package accumulating

import AccumulatingRequest._
import ChainedRequest.ConstantRequestFunction


class AccumulatingRequest4[-In,+T1,+T2,+T3,+T4,+Out](
    last:Response[T4] => Request[T4,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext, uriResolvers:UriResolver):AccumulatedResponse4[T1,T2,T3,T4,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse4[T1,T2,T3,T4,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest4[In, T1,T2,T3,T4,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest4[In, T1,T2,T3,T4, A] =
    new AccumulatingRequest4(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest5[In,T1,T2,T3,T4,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest5[In,T1,T2,T3,T4,Out,A] =
  new AccumulatingRequest5[In,T1,T2,T3,T4,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def setIn[A <: In](in: A) =
    new AccumulatingRequest4[Any, T1,T2,T3,T4,Out](last, Elem(Request.const(in),false) +: elems: _*)


  def execute(in: In)(implicit context: ExecutionContext, uriResolvers:UriResolver):AccumulatedResponse4[T1,T2,T3,T4,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse4(
      trackedResponses(0).asInstanceOf[Response[T1]],
      trackedResponses(1).asInstanceOf[Response[T2]],
      trackedResponses(2).asInstanceOf[Response[T3]],
      trackedResponses(3).asInstanceOf[Response[T4]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}
