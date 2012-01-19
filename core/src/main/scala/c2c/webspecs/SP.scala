package c2c.webspecs


object SP {
  def apply(pair: (Any, Any)): SP = SP(pair._1, pair._2)
}

case class SP(n: Any, v: Any) extends Param[Any, String](
  n match {
    case s: Symbol => s.name
    case _ => n.toString
  }, _ => v.toString)
