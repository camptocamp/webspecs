package c2c.webspecs


object SP {
  def apply(pair:(String,Any)):SP = SP(pair._1,pair._2)
}
case class SP(n:String, v:Any) extends Param[Any,String](n, _ => v.toString)
