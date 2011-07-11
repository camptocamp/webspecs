package c2c.webspecs

object P {
  def apply[Out](pair:(String,Out)):P[Out] = P(pair._1,pair._2)
}
case class P[+Out](n:String, v:Out) extends Param[Any,Out](n, _ => v)
