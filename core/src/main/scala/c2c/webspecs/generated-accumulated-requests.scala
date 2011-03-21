package c2c.webspecs

import AccumulatingRequest._
import ChainedRequest.ConstantRequestFunction
import java.io.IOException    
    

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


  override def map[A <: In](in: A) =
    new AccumulatingRequest1(last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse1[T1,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse1(
      trackedResponses(0).asInstanceOf[Response[T1]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}

class AccumulatedResponse1[+T1,+Z](
    _1:Response[T1],
    val last:Response[Z])
  extends Tuple1(
    _1
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value
    )
}



class AccumulatingRequest2[-In,+T1,+T2,+Out](
    last:Response[T2] => Request[T2,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse2[T1,T2,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse2[T1,T2,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest2[In, T1,T2,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest2[In, T1,T2, A] =
    new AccumulatingRequest2(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest3[In,T1,T2,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest3[In,T1,T2,Out,A] =
  new AccumulatingRequest3[In,T1,T2,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def map[A <: In](in: A) =
    new AccumulatingRequest2(last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse2[T1,T2,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse2(
      trackedResponses(0).asInstanceOf[Response[T1]],
          trackedResponses(1).asInstanceOf[Response[T2]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}

class AccumulatedResponse2[+T1,+T2,+Z](
    _1:Response[T1],
        _2:Response[T2],
    val last:Response[Z])
  extends Tuple2(
    _1,
        _2
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value
    )
}



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


  override def map[A <: In](in: A) =
    new AccumulatingRequest3(last, Elem(Request.const(in),false) +: elems: _*)


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

class AccumulatedResponse3[+T1,+T2,+T3,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
    val last:Response[Z])
  extends Tuple3(
    _1,
        _2,
        _3
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value
    )
}



class AccumulatingRequest4[-In,+T1,+T2,+T3,+T4,+Out](
    last:Response[T4] => Request[T4,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse4[T1,T2,T3,T4,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse4[T1,T2,T3,T4,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest4[In, T1,T2,T3,T4,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest4[In, T1,T2,T3,T4, A] =
    new AccumulatingRequest4(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest5[In,T1,T2,T3,T4,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest5[In,T1,T2,T3,T4,Out,A] =
  new AccumulatingRequest5[In,T1,T2,T3,T4,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def map[A <: In](in: A) =
    new AccumulatingRequest4(last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse4[T1,T2,T3,T4,Out] = {
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

class AccumulatedResponse4[+T1,+T2,+T3,+T4,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
    val last:Response[Z])
  extends Tuple4(
    _1,
        _2,
        _3,
        _4
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value
    )
}



class AccumulatingRequest5[-In,+T1,+T2,+T3,+T4,+T5,+Out](
    last:Response[T5] => Request[T5,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse5[T1,T2,T3,T4,T5,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse5[T1,T2,T3,T4,T5,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest5[In, T1,T2,T3,T4,T5,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest5[In, T1,T2,T3,T4,T5, A] =
    new AccumulatingRequest5(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest6[In,T1,T2,T3,T4,T5,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest6[In,T1,T2,T3,T4,T5,Out,A] =
  new AccumulatingRequest6[In,T1,T2,T3,T4,T5,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def map[A <: In](in: A) =
    new AccumulatingRequest5(last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse5[T1,T2,T3,T4,T5,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse5(
      trackedResponses(0).asInstanceOf[Response[T1]],
          trackedResponses(1).asInstanceOf[Response[T2]],
          trackedResponses(2).asInstanceOf[Response[T3]],
          trackedResponses(3).asInstanceOf[Response[T4]],
          trackedResponses(4).asInstanceOf[Response[T5]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}

class AccumulatedResponse5[+T1,+T2,+T3,+T4,+T5,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
    val last:Response[Z])
  extends Tuple5(
    _1,
        _2,
        _3,
        _4,
        _5
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value
    )
}



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


  override def map[A <: In](in: A) =
    new AccumulatingRequest6(last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse6[T1,T2,T3,T4,T5,T6,Out] = {
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

class AccumulatedResponse6[+T1,+T2,+T3,+T4,+T5,+T6,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
        _6:Response[T6],
    val last:Response[Z])
  extends Tuple6(
    _1,
        _2,
        _3,
        _4,
        _5,
        _6
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value,
          _6.value
    )
}



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


  override def map[A <: In](in: A) =
    new AccumulatingRequest7(last, Elem(Request.const(in),false) +: elems: _*)


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

class AccumulatedResponse7[+T1,+T2,+T3,+T4,+T5,+T6,+T7,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
        _6:Response[T6],
        _7:Response[T7],
    val last:Response[Z])
  extends Tuple7(
    _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value,
          _6.value,
          _7.value
    )
}



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


  override def map[A <: In](in: A) =
    new AccumulatingRequest8(last, Elem(Request.const(in),false) +: elems: _*)


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

class AccumulatedResponse8[+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
        _6:Response[T6],
        _7:Response[T7],
        _8:Response[T8],
    val last:Response[Z])
  extends Tuple8(
    _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7,
        _8
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value,
          _6.value,
          _7.value,
          _8.value
    )
}



class AccumulatingRequest9[-In,+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+Out](
    last:Response[T9] => Request[T9,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse9[T1,T2,T3,T4,T5,T6,T7,T8,T9,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse9[T1,T2,T3,T4,T5,T6,T7,T8,T9,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest9[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest9[In, T1,T2,T3,T4,T5,T6,T7,T8,T9, A] =
    new AccumulatingRequest9(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest10[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest10[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,Out,A] =
  new AccumulatingRequest10[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def map[A <: In](in: A) =
    new AccumulatingRequest9(last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse9[T1,T2,T3,T4,T5,T6,T7,T8,T9,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse9(
      trackedResponses(0).asInstanceOf[Response[T1]],
          trackedResponses(1).asInstanceOf[Response[T2]],
          trackedResponses(2).asInstanceOf[Response[T3]],
          trackedResponses(3).asInstanceOf[Response[T4]],
          trackedResponses(4).asInstanceOf[Response[T5]],
          trackedResponses(5).asInstanceOf[Response[T6]],
          trackedResponses(6).asInstanceOf[Response[T7]],
          trackedResponses(7).asInstanceOf[Response[T8]],
          trackedResponses(8).asInstanceOf[Response[T9]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}

class AccumulatedResponse9[+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
        _6:Response[T6],
        _7:Response[T7],
        _8:Response[T8],
        _9:Response[T9],
    val last:Response[Z])
  extends Tuple9(
    _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7,
        _8,
        _9
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value,
          _6.value,
          _7.value,
          _8.value,
          _9.value
    )
}



class AccumulatingRequest10[-In,+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+Out](
    last:Response[T10] => Request[T10,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse10[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse10[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest10[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest10[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10, A] =
    new AccumulatingRequest10(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest11[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest11[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,Out,A] =
  new AccumulatingRequest11[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def map[A <: In](in: A) =
    new AccumulatingRequest10(last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse10[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse10(
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
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}

class AccumulatedResponse10[+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
        _6:Response[T6],
        _7:Response[T7],
        _8:Response[T8],
        _9:Response[T9],
        _10:Response[T10],
    val last:Response[Z])
  extends Tuple10(
    _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7,
        _8,
        _9,
        _10
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value,
          _6.value,
          _7.value,
          _8.value,
          _9.value,
          _10.value
    )
}



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


  override def map[A <: In](in: A) =
    new AccumulatingRequest11(last, Elem(Request.const(in),false) +: elems: _*)


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

class AccumulatedResponse11[+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
        _6:Response[T6],
        _7:Response[T7],
        _8:Response[T8],
        _9:Response[T9],
        _10:Response[T10],
        _11:Response[T11],
    val last:Response[Z])
  extends Tuple11(
    _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7,
        _8,
        _9,
        _10,
        _11
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value,
          _6.value,
          _7.value,
          _8.value,
          _9.value,
          _10.value,
          _11.value
    )
}



class AccumulatingRequest12[-In,+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+Out](
    last:Response[T12] => Request[T12,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse12[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse12[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest12[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest12[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12, A] =
    new AccumulatingRequest12(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest13[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest13[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,Out,A] =
  new AccumulatingRequest13[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def map[A <: In](in: A) =
    new AccumulatingRequest12(last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse12[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse12(
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
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}

class AccumulatedResponse12[+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
        _6:Response[T6],
        _7:Response[T7],
        _8:Response[T8],
        _9:Response[T9],
        _10:Response[T10],
        _11:Response[T11],
        _12:Response[T12],
    val last:Response[Z])
  extends Tuple12(
    _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7,
        _8,
        _9,
        _10,
        _11,
        _12
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value,
          _6.value,
          _7.value,
          _8.value,
          _9.value,
          _10.value,
          _11.value,
          _12.value
    )
}



class AccumulatingRequest13[-In,+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+Out](
    last:Response[T13] => Request[T13,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse13[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse13[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest13[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest13[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13, A] =
    new AccumulatingRequest13(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest14[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest14[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,Out,A] =
  new AccumulatingRequest14[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def map[A <: In](in: A) =
    new AccumulatingRequest13(last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse13[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse13(
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
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}

class AccumulatedResponse13[+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
        _6:Response[T6],
        _7:Response[T7],
        _8:Response[T8],
        _9:Response[T9],
        _10:Response[T10],
        _11:Response[T11],
        _12:Response[T12],
        _13:Response[T13],
    val last:Response[Z])
  extends Tuple13(
    _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7,
        _8,
        _9,
        _10,
        _11,
        _12,
        _13
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value,
          _6.value,
          _7.value,
          _8.value,
          _9.value,
          _10.value,
          _11.value,
          _12.value,
          _13.value
    )
}



class AccumulatingRequest14[-In,+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+Out](
    last:Response[T14] => Request[T14,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse14[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse14[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest14[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest14[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14, A] =
    new AccumulatingRequest14(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest15[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest15[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,Out,A] =
  new AccumulatingRequest15[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def map[A <: In](in: A) =
    new AccumulatingRequest14(last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse14[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse14(
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
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}

class AccumulatedResponse14[+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
        _6:Response[T6],
        _7:Response[T7],
        _8:Response[T8],
        _9:Response[T9],
        _10:Response[T10],
        _11:Response[T11],
        _12:Response[T12],
        _13:Response[T13],
        _14:Response[T14],
    val last:Response[Z])
  extends Tuple14(
    _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7,
        _8,
        _9,
        _10,
        _11,
        _12,
        _13,
        _14
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value,
          _6.value,
          _7.value,
          _8.value,
          _9.value,
          _10.value,
          _11.value,
          _12.value,
          _13.value,
          _14.value
    )
}



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


  override def map[A <: In](in: A) =
    new AccumulatingRequest15(last, Elem(Request.const(in),false) +: elems: _*)


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

class AccumulatedResponse15[+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+T15,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
        _6:Response[T6],
        _7:Response[T7],
        _8:Response[T8],
        _9:Response[T9],
        _10:Response[T10],
        _11:Response[T11],
        _12:Response[T12],
        _13:Response[T13],
        _14:Response[T14],
        _15:Response[T15],
    val last:Response[Z])
  extends Tuple15(
    _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7,
        _8,
        _9,
        _10,
        _11,
        _12,
        _13,
        _14,
        _15
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value,
          _6.value,
          _7.value,
          _8.value,
          _9.value,
          _10.value,
          _11.value,
          _12.value,
          _13.value,
          _14.value,
          _15.value
    )
}



class AccumulatingRequest16[-In,+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+T15,+T16,+Out](
    last:Response[T16] => Request[T16,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse16[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse16[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest16[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest16[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16, A] =
    new AccumulatingRequest16(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest17[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest17[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,Out,A] =
  new AccumulatingRequest17[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def map[A <: In](in: A) =
    new AccumulatingRequest16(last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse16[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse16(
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
          trackedResponses(15).asInstanceOf[Response[T16]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}

class AccumulatedResponse16[+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+T15,+T16,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
        _6:Response[T6],
        _7:Response[T7],
        _8:Response[T8],
        _9:Response[T9],
        _10:Response[T10],
        _11:Response[T11],
        _12:Response[T12],
        _13:Response[T13],
        _14:Response[T14],
        _15:Response[T15],
        _16:Response[T16],
    val last:Response[Z])
  extends Tuple16(
    _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7,
        _8,
        _9,
        _10,
        _11,
        _12,
        _13,
        _14,
        _15,
        _16
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value,
          _6.value,
          _7.value,
          _8.value,
          _9.value,
          _10.value,
          _11.value,
          _12.value,
          _13.value,
          _14.value,
          _15.value,
          _16.value
    )
}



class AccumulatingRequest17[-In,+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+T15,+T16,+T17,+Out](
    last:Response[T17] => Request[T17,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse17[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse17[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest17[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest17[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17, A] =
    new AccumulatingRequest17(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest18[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest18[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,Out,A] =
  new AccumulatingRequest18[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def map[A <: In](in: A) =
    new AccumulatingRequest17(last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse17[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse17(
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
          trackedResponses(15).asInstanceOf[Response[T16]],
          trackedResponses(16).asInstanceOf[Response[T17]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}

class AccumulatedResponse17[+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+T15,+T16,+T17,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
        _6:Response[T6],
        _7:Response[T7],
        _8:Response[T8],
        _9:Response[T9],
        _10:Response[T10],
        _11:Response[T11],
        _12:Response[T12],
        _13:Response[T13],
        _14:Response[T14],
        _15:Response[T15],
        _16:Response[T16],
        _17:Response[T17],
    val last:Response[Z])
  extends Tuple17(
    _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7,
        _8,
        _9,
        _10,
        _11,
        _12,
        _13,
        _14,
        _15,
        _16,
        _17
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value,
          _6.value,
          _7.value,
          _8.value,
          _9.value,
          _10.value,
          _11.value,
          _12.value,
          _13.value,
          _14.value,
          _15.value,
          _16.value,
          _17.value
    )
}



class AccumulatingRequest18[-In,+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+T15,+T16,+T17,+T18,+Out](
    last:Response[T18] => Request[T18,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse18[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse18[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest18[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest18[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18, A] =
    new AccumulatingRequest18(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest19[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest19[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,Out,A] =
  new AccumulatingRequest19[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def map[A <: In](in: A) =
    new AccumulatingRequest18(last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse18[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse18(
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
          trackedResponses(15).asInstanceOf[Response[T16]],
          trackedResponses(16).asInstanceOf[Response[T17]],
          trackedResponses(17).asInstanceOf[Response[T18]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}

class AccumulatedResponse18[+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+T15,+T16,+T17,+T18,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
        _6:Response[T6],
        _7:Response[T7],
        _8:Response[T8],
        _9:Response[T9],
        _10:Response[T10],
        _11:Response[T11],
        _12:Response[T12],
        _13:Response[T13],
        _14:Response[T14],
        _15:Response[T15],
        _16:Response[T16],
        _17:Response[T17],
        _18:Response[T18],
    val last:Response[Z])
  extends Tuple18(
    _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7,
        _8,
        _9,
        _10,
        _11,
        _12,
        _13,
        _14,
        _15,
        _16,
        _17,
        _18
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value,
          _6.value,
          _7.value,
          _8.value,
          _9.value,
          _10.value,
          _11.value,
          _12.value,
          _13.value,
          _14.value,
          _15.value,
          _16.value,
          _17.value,
          _18.value
    )
}



class AccumulatingRequest19[-In,+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+T15,+T16,+T17,+T18,+T19,+Out](
    last:Response[T19] => Request[T19,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse19[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse19[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest19[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest19[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19, A] =
    new AccumulatingRequest19(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest20[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest20[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,Out,A] =
  new AccumulatingRequest20[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def map[A <: In](in: A) =
    new AccumulatingRequest19(last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse19[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse19(
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
          trackedResponses(15).asInstanceOf[Response[T16]],
          trackedResponses(16).asInstanceOf[Response[T17]],
          trackedResponses(17).asInstanceOf[Response[T18]],
          trackedResponses(18).asInstanceOf[Response[T19]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}

class AccumulatedResponse19[+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+T15,+T16,+T17,+T18,+T19,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
        _6:Response[T6],
        _7:Response[T7],
        _8:Response[T8],
        _9:Response[T9],
        _10:Response[T10],
        _11:Response[T11],
        _12:Response[T12],
        _13:Response[T13],
        _14:Response[T14],
        _15:Response[T15],
        _16:Response[T16],
        _17:Response[T17],
        _18:Response[T18],
        _19:Response[T19],
    val last:Response[Z])
  extends Tuple19(
    _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7,
        _8,
        _9,
        _10,
        _11,
        _12,
        _13,
        _14,
        _15,
        _16,
        _17,
        _18,
        _19
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value,
          _6.value,
          _7.value,
          _8.value,
          _9.value,
          _10.value,
          _11.value,
          _12.value,
          _13.value,
          _14.value,
          _15.value,
          _16.value,
          _17.value,
          _18.value,
          _19.value
    )
}



class AccumulatingRequest20[-In,+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+T15,+T16,+T17,+T18,+T19,+T20,+Out](
    last:Response[T20] => Request[T20,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse20[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse20[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest20[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest20[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20, A] =
    new AccumulatingRequest20(next, elems :+ new Elem(last,false) :_*)

def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest21[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest21[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,Out,A] =
  new AccumulatingRequest21[In,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,Out,A](next,elems :+ new Elem(last,true) :_*)


  override def map[A <: In](in: A) =
    new AccumulatingRequest20(last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse20[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse20(
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
          trackedResponses(15).asInstanceOf[Response[T16]],
          trackedResponses(16).asInstanceOf[Response[T17]],
          trackedResponses(17).asInstanceOf[Response[T18]],
          trackedResponses(18).asInstanceOf[Response[T19]],
          trackedResponses(19).asInstanceOf[Response[T20]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}

class AccumulatedResponse20[+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+T15,+T16,+T17,+T18,+T19,+T20,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
        _6:Response[T6],
        _7:Response[T7],
        _8:Response[T8],
        _9:Response[T9],
        _10:Response[T10],
        _11:Response[T11],
        _12:Response[T12],
        _13:Response[T13],
        _14:Response[T14],
        _15:Response[T15],
        _16:Response[T16],
        _17:Response[T17],
        _18:Response[T18],
        _19:Response[T19],
        _20:Response[T20],
    val last:Response[Z])
  extends Tuple20(
    _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7,
        _8,
        _9,
        _10,
        _11,
        _12,
        _13,
        _14,
        _15,
        _16,
        _17,
        _18,
        _19,
        _20
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value,
          _6.value,
          _7.value,
          _8.value,
          _9.value,
          _10.value,
          _11.value,
          _12.value,
          _13.value,
          _14.value,
          _15.value,
          _16.value,
          _17.value,
          _18.value,
          _19.value,
          _20.value
    )
}



class AccumulatingRequest21[-In,+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+T15,+T16,+T17,+T18,+T19,+T20,+T21,+Out](
    last:Response[T21] => Request[T21,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse21[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse21[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest21[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest21[In, T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21, A] =
    new AccumulatingRequest21(next, elems :+ new Elem(last,false) :_*)



  override def map[A <: In](in: A) =
    new AccumulatingRequest21(last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse21[T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20,T21,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse21(
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
          trackedResponses(15).asInstanceOf[Response[T16]],
          trackedResponses(16).asInstanceOf[Response[T17]],
          trackedResponses(17).asInstanceOf[Response[T18]],
          trackedResponses(18).asInstanceOf[Response[T19]],
          trackedResponses(19).asInstanceOf[Response[T20]],
          trackedResponses(20).asInstanceOf[Response[T21]], 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}

class AccumulatedResponse21[+T1,+T2,+T3,+T4,+T5,+T6,+T7,+T8,+T9,+T10,+T11,+T12,+T13,+T14,+T15,+T16,+T17,+T18,+T19,+T20,+T21,+Z](
    _1:Response[T1],
        _2:Response[T2],
        _3:Response[T3],
        _4:Response[T4],
        _5:Response[T5],
        _6:Response[T6],
        _7:Response[T7],
        _8:Response[T8],
        _9:Response[T9],
        _10:Response[T10],
        _11:Response[T11],
        _12:Response[T12],
        _13:Response[T13],
        _14:Response[T14],
        _15:Response[T15],
        _16:Response[T16],
        _17:Response[T17],
        _18:Response[T18],
        _19:Response[T19],
        _20:Response[T20],
        _21:Response[T21],
    val last:Response[Z])
  extends Tuple21(
    _1,
        _2,
        _3,
        _4,
        _5,
        _6,
        _7,
        _8,
        _9,
        _10,
        _11,
        _12,
        _13,
        _14,
        _15,
        _16,
        _17,
        _18,
        _19,
        _20,
        _21
  ) with AccumulatedResponse[Z] {
    def values = (
      _1.value,
          _2.value,
          _3.value,
          _4.value,
          _5.value,
          _6.value,
          _7.value,
          _8.value,
          _9.value,
          _10.value,
          _11.value,
          _12.value,
          _13.value,
          _14.value,
          _15.value,
          _16.value,
          _17.value,
          _18.value,
          _19.value,
          _20.value,
          _21.value
    )
}
