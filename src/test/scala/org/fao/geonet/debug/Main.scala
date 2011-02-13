package org.fao.geonet
package debug


object AccDivCountFactory extends ValueFactory[Int,Int] {
  def apply[A <: Any, B >: Int](request: Request[A, B], in: Int, rawValue: BasicHttpValue) =
    in + XmlValue(rawValue).withXml {_ \\ "div" size}
}
case class DivCount(uri:String)
  extends AbstractGetRequest(uri,SelfValueFactory[Any,Int]())
  with ValueFactory[Any,Int] {

 def apply[A <: Any, B >: Int](request: Request[A, B], in: Any, rawValue: BasicHttpValue) =
    XmlValue(rawValue).withXml {_ \\ "div" size}
}

case class DivCountAcc(uri:String) extends AbstractGetRequest[Int,Int](uri,AccDivCountFactory)

object Main extends Application {
   val count = (DivCount("http://www.scala-lang.org") then DivCountAcc("http://www.google.com"))(None).value
  println(count)
}