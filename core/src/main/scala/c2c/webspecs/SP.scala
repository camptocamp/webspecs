package c2c.webspecs


object SP {
  def apply(pair: (Any, Any)): SP = SP(pair._1, pair._2)

  private def toString(a:Any) = a match {
    case s: Symbol => s.name
    case _ => a.toString
  }
}

case class SP(n: Any, v: Any) extends Param[Any, String](
  SP.toString(n), _ => SP.toString(v))
