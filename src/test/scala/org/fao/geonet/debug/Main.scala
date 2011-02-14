package org.fao.geonet
package debug



class AccDivCountFactory(polar:Int) extends ValueFactory[Int,Int] {
  def apply[A <: Any, B >: Int](request: Request[A, B], in: Int, rawValue: BasicHttpValue) =
    in + polar*XmlValue(rawValue).withXml {_ \\ "div" size}
}
case class DivCount(uri:String)
  extends AbstractGetRequest(uri,SelfValueFactory[Any,Int]())
  with ValueFactory[Any,Int] {

 def apply[A <: Any, B >: Int](request: Request[A, B], in: Any, rawValue: BasicHttpValue) =
    XmlValue(rawValue).withXml {_ \\ "div" size}
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
  val count = (AddCookie then DivCount("http://localhost:43080/geonetwork") then { response =>
    if(response.value > 5) DivCountAcc("http://localhost:43080/cas/login",-1)
    else DivCountAcc("http://localhost:43080/cas/login",-1)
  })(None).value
  println(count)
  context.httpClient.getConnectionManager.shutdown
}