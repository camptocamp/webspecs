package c2c.webspecs
package debug



class AccDivCountFactory(polar:Int) extends ValueFactory[Int,Int] {
  def createValue[A <: Any, B >: Int](request: Request[A,  B], in: Int, rawValue: BasicHttpValue,executionContext:ExecutionContext) = polar
}
case class DivCount(uri:String)
  extends AbstractGetRequest(uri,SelfValueFactory[Any,Int]())
  with ValueFactory[Any,Int] {

 def createValue[A <: Any, B >: Int](request: Request[A, B], in: Any, rawValue: BasicHttpValue,executionContext:ExecutionContext) = 0
}

case class DivCountAcc(uri:String,polar:Int) extends AbstractGetRequest[Int,Int](uri,new AccDivCountFactory(polar))

object AddCookie extends Request[Any,Null] {
  def apply(in: Any)(implicit context: ExecutionContext) = {
    context.modifications ::= RequestModification(_.addHeader("addedHeader","the value"))
    EmptyResponse
  }
}

object Main extends Application {
  implicit val context = DefaultExecutionContext()
  val req = (AddCookie then DivCount("http://localhost:43080/cas/logout") trackThen DivCountAcc("http://localhost:43080/cas/login",2)
    then DivCountAcc("http://localhost:43080/cas/login",3) trackThen DivCountAcc("http://localhost:43080/cas/login",4) trackThen DivCountAcc("http://localhost:43080/cas/login",5))
  val count = req(None) match {
    case AccumulatedResponse.IncludeLast(one,two,three,four) => println(one.value,two.value,three.value,four.value)
  }
/*  val count = (AddCookie then DivCount("http://localhost:43080/cas/logout") trackThen DivCountAcc("http://localhost:43080/cas/login",-1)
    then DivCountAcc("http://localhost:43080/cas/login",1) then DivCountAcc("http://localhost:43080/cas/login",1) trackThen DivCountAcc("http://localhost:43080/cas/login",1))(None) match {
    case AccumulatedResponse.IncludeLast(one,two,three) => println(one.value,two.value,three.value)
  }*/
  context.httpClient.getConnectionManager.shutdown
}