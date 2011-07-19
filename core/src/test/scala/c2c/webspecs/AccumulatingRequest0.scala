package c2c.webspecs

import c2c.webspecs.ChainedRequest.ConstantRequestFunction
import c2c.webspecs.accumulating.AccumulatingRequest1
import java.io.IOException
import c2c.webspecs.AccumulatingRequest.Elem

class AccumulatingRequest0[-In,+Out](last:Response[Any] => Request[Any,Out],
                                     elems:Elem*)
  extends AccumulatingRequest[In,Out] {

  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest0[In, A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest0[In, A] =
    new AccumulatingRequest0(
      next.asInstanceOf[Response[Any] => Request[Any,A]],
      elems :+ new Elem(last,false) :_*)

  def trackThen [A,B] (next: Request[Out,A]) : AccumulatingRequest1[In,Out,A] =
    trackThen(new ConstantRequestFunction(next))
  def trackThen [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest1[In,Out,A] =
    new AccumulatingRequest1(next, elems :+ new Elem(last,true) :_*)
  override def setIn[A <: In](in: A) =
    new AccumulatingRequest0[Any,Out](last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):Response[Out] = {
    val resultData = doApply(in,last.asInstanceOf[RequestFactory],elems)
    resultData.lastResponse.asInstanceOf[Response[Out]]
  }

  override def toString() = elems.mkString("(",",",")")+"then"+last
}