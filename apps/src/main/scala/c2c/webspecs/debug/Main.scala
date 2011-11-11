package c2c.webspecs
package debug



class AccDivCountFactory(polar:Int) extends BasicValueFactory[Int] {
  override def createValue(rawValue: BasicHttpValue) = polar
}
case class DivCount(uri:String)
  extends AbstractGetRequest(uri,SelfValueFactory[Any,Int]())
  with BasicValueFactory[Int] {

 override def createValue(rawValue: BasicHttpValue) = 0
}

case class DivCountAcc(uri:String,polar:Int) extends AbstractGetRequest[Int,Int](uri,new AccDivCountFactory(polar))

object AddCookie extends Request[Any,Null] {
  def execute(in: Any)(implicit context: ExecutionContext, uriResolver:UriResolver) = {
    context.modifications ::= RequestModification(_.addHeader("addedHeader","the value"))
    EmptyResponse
  }
}

object Main extends WebspecsApp {
  val req = (AddCookie then
    DivCount("http://localhost:43080/cas/logout") startTrackingThen
    DivCountAcc("http://localhost:43080/cas/login",2) then
    DivCountAcc("http://localhost:43080/cas/login",3) trackThen
    DivCountAcc("http://localhost:43080/cas/login",4) trackThen
    DivCountAcc("http://localhost:43080/cas/login",5))
  val response = req.execute()

  println(response.values)
}