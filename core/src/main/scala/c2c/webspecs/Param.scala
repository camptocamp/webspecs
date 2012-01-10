package c2c.webspecs

abstract class Param[-In,+Out](val name:String,val value:In => Out)
object Param {
  private def asString(obj:Any) = obj match {
    case symbol:Symbol => symbol.name
    case _ => obj.toString
  }
  def stringMapping[A,B] = (p:(A,B)) => P(asString(p._1),asString(p._2))
}
